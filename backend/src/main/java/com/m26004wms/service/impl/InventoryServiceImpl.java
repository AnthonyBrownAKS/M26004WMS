package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.m26004wms.common.Result;
import com.m26004wms.dto.InventoryPageDTO;
import com.m26004wms.entity.*;
import com.m26004wms.mapper.*;
import com.m26004wms.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private MaterialContainerMapper materialContainerMapper;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private CustomerSupplierMapper customerSupplierMapper;

    @Autowired
    private LocationMapper locationMapper;


    /**
     * 分页
     */
    @Override
    public Object page(Integer current, Integer size, String containerId, String locationAreaId) {

        int offset = (current - 1) * size;

        containerId = containerId == null ? "" : containerId;
        locationAreaId = locationAreaId == null ? "" : locationAreaId;

        List<Inventory> records = inventoryMapper.selectPage(offset, size, containerId, locationAreaId);
        int total = inventoryMapper.selectCount(containerId);

        List<InventoryData> data = new LinkedList<>();
        Inventory record;

        // 包装数据
        for (int i = 0; i < total; i++) {
            record = records.get(i);

            String container = record.getContainerId();
            List<MaterialContainer> mc = inventoryMapper.getData(container);
            InventoryData ida = new InventoryData();

            ida.setId(record.getId());
            ida.setSum(mc.size());

            ida.setContainerId(container);

            ida.setLocationAreaId(record.getLocationAreaId());
            ida.setRowNo(record.getRowNo());
            ida.setColumnNo(record.getColumnNo());
            ida.setLayerNo(record.getLayerNo());
            ida.setCreationTime(record.getCreationTime());

            ida.setContainerId(record.getContainerId());

            data.add(ida);
        }

        long pages = (total + size - 1) / size;

        Map<String, Object> result =
                new HashMap<>();

        result.put("records", data);

        result.put("current", current);

        result.put("pages", pages);

        result.put("size", size);

        result.put("total", total);

        return result;

    }

    /**
     * 新建或修改库存
     */
    @Override
    public String add(InventoryData inventoryData) {

        Inventory in = new Inventory();
        in.setLocationAreaId("A");
        in.setLocationId(inventoryData.getLocationAreaId());
        in.setRowNo(inventoryData.getRowNo());
        in.setColumnNo(inventoryData.getColumnNo());
        in.setLayerNo(inventoryData.getLayerNo());
        in.setContainerId(inventoryData.getContainerId());

        if (inventoryData.getId() != null){
            in.setId(inventoryData.getId());
            inventoryMapper.insertOrUpdate(in);
            return "修改成功";
        }

        if (inventoryMapper.exitContainer(inventoryData.getContainerId()).isEmpty()){
            in.setCreationTime(LocalDateTime.now());
            inventoryMapper.insertOrUpdate(in);
        }

        return "添加成功";
    }


    /**
     * 获取详细数据
     */
    @Override
    public List<InventoryData> getData(String containerId) {

        List<MaterialContainer> list = inventoryMapper.getData(containerId);

        List<InventoryData> result = new LinkedList<>();
        Material m;
        for (MaterialContainer mc : list) {
            m = materialMapper.getByCustomerCode(mc.getMaterialCode());
            InventoryData d = new InventoryData();

            d.setId(mc.getId());
            d.setMaterialCode(m.getCode());
            d.setMaterialName(m.getName());
            d.setSpec(m.getSpec());

            d.setCustomerCode(mc.getCustomerCode());
            d.setQuantity(mc.getQuantity());
            d.setBatch(mc.getBatch());
            d.setCreationTime(mc.getCreationTime());
            d.setContainerId(containerId);

            d.setSum(1);

            result.add(d);
        }

        return result;
    }

    @Override
    public String del(int id) {
        Inventory in = inventoryMapper.selectById(id);
        String container = in.getContainerId();
        List<MaterialContainer> list = inventoryMapper.getData(container);

        for (MaterialContainer mc : list){
            materialContainerMapper.deleteById(mc.getId());

        }
        inventoryMapper.deleteById(id);
        return "删除成功";
    }


}
