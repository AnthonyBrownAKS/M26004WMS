package com.m26004wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.m26004wms.entity.Inventory;

public interface InventoryService {

    /**
     * 分页显示
     */
    Page<Inventory> getInventoryPage(
            Long current,
            Long size
    );
}
