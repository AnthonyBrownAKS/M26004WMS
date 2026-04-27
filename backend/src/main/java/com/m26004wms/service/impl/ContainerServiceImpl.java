package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.m26004wms.dto.ContainerExcelDTO;
import com.m26004wms.entity.Container;
import com.m26004wms.mapper.ContainerMapper;
import com.m26004wms.service.ContainerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContainerServiceImpl
        extends ServiceImpl<ContainerMapper, Container>
        implements ContainerService {

    @Transactional
    @Override
    public void importExcel(List<ContainerExcelDTO> list) {

        if (list == null || list.isEmpty()) return;

        // =========================
        // Excel去重（按code）
        // =========================
        Map<String, ContainerExcelDTO> dtoMap = list.stream()
                .filter(d -> d.getCode() != null && !d.getCode().isEmpty())
                .collect(Collectors.toMap(
                        ContainerExcelDTO::getCode,
                        v -> v,
                        (v1, v2) -> v1
                ));

        List<String> codes = new ArrayList<>(dtoMap.keySet());

        // =========================
        // 查询数据库
        // =========================
        Map<String, Container> existMap = this.lambdaQuery()
                .in(Container::getCode, codes)
                .list()
                .stream()
                .collect(Collectors.toMap(
                        Container::getCode,
                        v -> v,
                        (v1, v2) -> v1
                ));

        List<Container> insertList = new ArrayList<>();
        List<Container> updateList = new ArrayList<>();

        // =========================
        // DTO → Entity
        // =========================
        for (ContainerExcelDTO dto : dtoMap.values()) {

            Container e = new Container();

            e.setId(dto.getId());
            e.setWarehouseId(dto.getWarehouseId());
            e.setCode(dto.getCode());
            e.setBarcode(dto.getBarcode());
            e.setContainerTypeId(dto.getContainerTypeId());
            e.setLoadState(dto.getLoadState());
            e.setLocationId(dto.getLocationId());

            e.setIsStack(dto.getIsStack());
            e.setIsDeleted(dto.getIsDeleted());

            e.setDeleterId(dto.getDeleterId());
            e.setDeletionTime(parseTime(dto.getDeletionTime()));

            e.setLastModificationTime(parseTime(dto.getLastModificationTime()));
            e.setLastModifierId(dto.getLastModifierId());

            e.setCreationTime(parseTime(dto.getCreationTime()));
            e.setCreatorId(dto.getCreatorId());

            e.setExtraProperties(dto.getExtraProperties());
            e.setConcurrencyStamp(dto.getConcurrencyStamp());

            Container exist = existMap.get(dto.getCode());

            if (exist != null) {
                e.setId(exist.getId());
                updateList.add(e);
            } else {
                insertList.add(e);
            }
        }

        // =========================
        // 批量操作
        // =========================
        if (!insertList.isEmpty()) {
            this.saveBatch(insertList, 1000);
        }

        if (!updateList.isEmpty()) {
            updateList.forEach(this::updateById);
        }
    }

    @Override
    public List<Container> exportAll() {
        return this.list();
    }

    // =========================
    // 时间解析（复用你之前那套）
    // =========================
    private LocalDateTime parseTime(String v) {
        if (v == null || v.trim().isEmpty()) return null;

        try {
            return LocalDateTime.parse(v,
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss"));
        } catch (Exception ignored) {}

        try {
            return LocalDateTime.parse(v,
                    java.time.format.DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss"));
        } catch (Exception ignored) {}

        return null;
    }
}
