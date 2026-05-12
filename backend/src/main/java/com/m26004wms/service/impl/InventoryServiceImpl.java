package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.m26004wms.dto.InventoryPageDTO;
import com.m26004wms.entity.Inventory;
import com.m26004wms.mapper.InventoryMapper;
import com.m26004wms.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;

    @Override
    public Page<Inventory> getInventoryPage(Long current, Long size) {
        Page<Inventory> page = new Page<>(current, size);
        return inventoryMapper.selectPage(page, null);
    }
}
