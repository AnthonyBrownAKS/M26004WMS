package com.m26004wms.service;

import com.m26004wms.common.Result;
import com.m26004wms.entity.InventoryData;

import java.util.List;

public interface InventoryService {

    /**
     * 分页显示
     */
    Object page(
            Integer current,
            Integer size,
            String containerId,
            String locationAreaId
    );

    /**
     * 添加库存
     */
    String add(InventoryData inventoryData);

    /**
     * 通过containerId获取详细数据
     */
    List<InventoryData> getData(String containerId);

    /**
     * 通过id删除库存
     */
    String del(int id);
}
