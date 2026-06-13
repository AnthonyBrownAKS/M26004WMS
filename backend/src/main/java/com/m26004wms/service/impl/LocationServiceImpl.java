package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.m26004wms.common.QW;
import com.m26004wms.dto.LocationExcelDTO;
import com.m26004wms.dto.LocationQueryDTO;
import com.m26004wms.entity.Location;
import com.m26004wms.mapper.LocationMapper;
import com.m26004wms.service.LocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl
        extends ServiceImpl<LocationMapper, Location>
        implements LocationService {

    /**
     * 自定义信息查询
     *
     */

    @Override
    public List<Location> query(LocationQueryDTO dto) {

        LambdaQueryWrapper<Location> qw = new LambdaQueryWrapper<>();

        QW.eq(qw, dto.getWarehouseId(), Location::getWarehouseId);
        QW.like(qw, dto.getCode(), Location::getCode);
        QW.eq(qw, dto.getDirection(), Location::getDirection);

        return this.list(qw);
    }


    // =========================
    // Excel导入
    // =========================
    @Transactional
    @Override
    public void importExcel(List<LocationExcelDTO> list) {

        if (list == null || list.isEmpty()) {
            return;
        }

        // =========================
        // Excel内部去重（按Code）
        // =========================
        Map<String, LocationExcelDTO> dtoMap = list.stream()
                .filter(dto -> dto.getCode() != null && !dto.getCode().isEmpty())
                .collect(Collectors.toMap(
                        LocationExcelDTO::getCode,
                        v -> v,
                        (v1, v2) -> v1
                ));

        List<String> codes = new ArrayList<>(dtoMap.keySet());

        // =========================
        // 查询数据库已有数据
        // =========================
        Map<String, Location> existMap = this.lambdaQuery()
                .in(Location::getCode, codes)
                .list()
                .stream()
                .collect(Collectors.toMap(
                        Location::getCode,
                        v -> v,
                        (v1, v2) -> v1
                ));

        List<Location> insertList = new ArrayList<>();
        List<Location> updateList = new ArrayList<>();

        // =========================
        // DTO → Entity + Upsert
        // =========================
        for (LocationExcelDTO dto : dtoMap.values()) {

            Location e = new Location();

            // ===== 主键处理 =====
            e.setId(dto.getId() != null ? dto.getId() : genId());

            e.setWarehouseId(dto.getWarehouseId());
            e.setLocationAreaId(dto.getLocationAreaId());

            e.setCode(dto.getCode());
            e.setShortCode(dto.getShortCode());

            e.setIsVirtually(bool(dto.getIsVirtually()));
            e.setDirection(dto.getDirection());

            e.setRow(dto.getRow());
            e.setColumn(dto.getColumn());
            e.setLayer(dto.getLayer());
            e.setDepth(dto.getDepth());
            e.setDepthNum(dto.getDepthNum());

            e.setMaxFullPalletNum(dto.getMaxFullPalletNum());
            e.setMaxEmptyPalletNum(dto.getMaxEmptyPalletNum());

            e.setIsAllowReceipt(bool(dto.getIsAllowReceipt()));
            e.setIsAllowSelect(bool(dto.getIsAllowSelect()));

            e.setReceiptMode(dto.getReceiptMode());
            e.setInboundMode(dto.getInboundMode());
            e.setInboundTrigMode(dto.getInboundTrigMode());

            e.setIsWithPallet(bool(dto.getIsWithPallet()));

            e.setAllowInbound(bool(dto.getAllowInbound()));
            e.setAllowOutbound(bool(dto.getAllowOutbound()));

            e.setCargoStatus(dto.getCargoStatus());
            e.setLockState(dto.getLockState());
            e.setLockContainerBarcode(dto.getLockContainerBarcode());

            e.setIsStocktake(bool(dto.getIsStocktake()));

            e.setX(dto.getX());
            e.setY(dto.getY());
            e.setWidth(dto.getWidth());
            e.setHeight(dto.getHeight());

            e.setIsDeleted(bool(dto.getIsDeleted()));

            // JSON校验
            e.setExtraProperties(dto.getExtraProperties());

            e.setConcurrencyStamp(dto.getConcurrencyStamp());

            Location exist = existMap.get(dto.getCode());

            if (exist != null) {
                // 更新
                e.setId(exist.getId());
                updateList.add(e);
            } else {
                // 新增
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
        // 批量更新
        // =========================
        if (!updateList.isEmpty()) {
            updateList.forEach(this::updateById);
        }
    }

    // =========================
    // 导出
    // =========================
    @Override
    public List<Location> exportAll() {
        return this.list();
    }

    // =========================
    // 工具方法
    // =========================

    private Boolean bool(Boolean v) {
        return v != null && v;
    }

    private String genId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
