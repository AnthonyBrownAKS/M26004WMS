package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.m26004wms.dto.MaterialExcelDTO;
import com.m26004wms.entity.Material;
import com.m26004wms.mapper.MaterialMapper;
import com.m26004wms.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    // 解决时间问题
    private static final List<DateTimeFormatter> FORMATTERS = List.of(

            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),

            DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss"),

            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),

            DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")

    );

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
    public static LocalDateTime parseTime(String value) {

        if (value == null || value.trim().isEmpty()) {

            return null;

        }

        value = value.trim();

        for (DateTimeFormatter formatter : FORMATTERS) {

            try {

                return LocalDateTime.parse(value, formatter);

            } catch (Exception ignored) {

            }

        }

        throw new RuntimeException(
                "无法解析时间格式: " + value
        );

    }

    @Override
    public Object page(Integer current, Integer size, String code) {

        int offset = (current - 1) * size;
        List<Material> records = materialMapper.selectPage(offset, size, code);

        Long total = materialMapper.selectCount(code);

        long pages = (total + size - 1) / size;

        Map<String, Object> result =
                new HashMap<>();

        result.put("records", records);

        result.put("current", current);

        result.put("pages", pages);

        result.put("size", size);

        result.put("total", total);

        return result;

    }


}
