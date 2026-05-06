package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.m26004wms.dto.MaterialExcelDTO;
import com.m26004wms.entity.Material;
import com.m26004wms.mapper.MaterialMapper;
import com.m26004wms.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaterialServiceImpl
        extends ServiceImpl<MaterialMapper, Material>
        implements MaterialService {


    @Autowired
    private MaterialMapper materialMapper;

    // 解决2025/5/20 07:15:36格式数据的转换
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");

    @Override
    public Material getMaterialById(String id) {
        return materialMapper.selectById(id);
    }

    // =========================
    // Excel导入
    // =========================
    @Transactional
    @Override
    public void importExcel(List<MaterialExcelDTO> list) {

        if (list == null || list.isEmpty()) {
            return;
        }

        // =========================
        // Excel内部去重（按code）
        // =========================
        Map<String, MaterialExcelDTO> dtoMap = list.stream()
                .filter(dto -> dto.getCode() != null && !dto.getCode().isEmpty())
                .collect(Collectors.toMap(
                        MaterialExcelDTO::getCode,
                        v -> v,
                        (v1, v2) -> v1   // 保留第一条
                ));

        List<String> codes = new ArrayList<>(dtoMap.keySet());

        // =========================
        // 查询数据库已有数据
        // =========================
        Map<String, Material> existMap = this.lambdaQuery()
                .in(Material::getCode, codes)
                .list()
                .stream()
                .collect(Collectors.toMap(
                        Material::getCode,
                        v -> v,
                        (v1, v2) -> v1   // 防止数据库已有重复
                ));

        List<Material> insertList = new ArrayList<>();
        List<Material> updateList = new ArrayList<>();

        // =========================
        // DTO → Entity + Upsert逻辑
        // =========================
        for (MaterialExcelDTO dto : dtoMap.values()) {

            Material e = new Material();

            e.setId(dto.getId());
            e.setCode(dto.getCode());
            e.setShortCode(dto.getShortCode());
            e.setName(dto.getName());
            e.setSpec(dto.getSpec());
            e.setIsUnique(dto.getIsUnique());
            e.setValidityDate(dto.getValidityDate());
            e.setAbcType(dto.getAbcType());
            e.setSafetyStock(dto.getSafetyStock());
            e.setCreationTime(parseTime(dto.getCreationTime()));

            Material exist = existMap.get(dto.getCode());

            if (exist != null) {
                // =========================
                // 更新
                // =========================
                e.setId(exist.getId());
                updateList.add(e);
            } else {
                // =========================
                // 新增
                // =========================
                insertList.add(e);
            }
        }

        // =========================
        // 批量插入
        // =========================
        if (!insertList.isEmpty()) {
            this.saveBatch(insertList, 1000);
        }

        // =========================
        // 批量更新（MP不支持批量update，只能循环）
        // =========================
        if (!updateList.isEmpty()) {
            updateList.forEach(this::updateById);
        }
    }

    // =========================
    // 导出
    // =========================
    @Override
    public List<Material> exportAll() {
        return this.list();
    }

    // 时间格式转换
    private LocalDateTime parseTime(String v) {
        if (v == null || v.trim().isEmpty()) {
            return null;
        }

        return LocalDateTime.parse(v, FORMATTER);
    }


}
