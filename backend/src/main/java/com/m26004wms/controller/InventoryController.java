package com.m26004wms.controller;


import com.m26004wms.common.LogUtil;
import com.m26004wms.common.Result;
import com.m26004wms.entity.Inventory;
import com.m26004wms.entity.InventoryData;
import com.m26004wms.entity.Logs;
import com.m26004wms.entity.Material;
import com.m26004wms.mapper.InventoryMapper;
import com.m26004wms.mapper.LogMapper;
import com.m26004wms.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryMapper inventoryMapper;
    private final InventoryService inventoryService;

    @Autowired
    private LogMapper logMapper;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<Object> page(

            @RequestParam Integer current,

            @RequestParam Integer size,

            @RequestParam(required = false) String containerId,

            @RequestParam(required = false) String locationAreaId
    ) {

        if (!containerId.isEmpty() || !locationAreaId.isEmpty()){
            // 操作日志
            LogUtil.success(
                    logMapper,
                    "SELECT",
                    "搜索库存SELECT DATA BY CONTAINER_ID( " + containerId + " ) AND LOCATION_AREA_ID( " + locationAreaId + " )"
            );
        }

        return Result.success(inventoryService.page(current, size, containerId, locationAreaId));

    }



    /**
     * 查询库存
     */
    @GetMapping("/search")
    public Result<Object> search(

            @RequestParam
            String materialCode,

            @RequestParam
            String batch

    ) {

        Map<String, Object> inventory = inventoryMapper.searchInventory(materialCode, batch);

        // 操作日志
        Logs log = new Logs();
        log.setType("SELECT");

        if (inventory == null) {
            log.setResult("FAIL: 未找到库存");
            log.setParam("SELECT DATA BY CODE( " + materialCode + " ) AND BATCH( " + batch + " )");
            logMapper.insertControl(log);
            return Result.fail("未找到库存");
        }

        log.setResult("SUCCESS");
        log.setParam("SELECT DATA BY CODE( " + materialCode + " ) AND BATCH( " + batch + " )");
        logMapper.insertControl(log);

        return Result.success(
                "查询成功",
                inventory
        );
    }

    /**
     * 新增/修改
     * 传入 materialCode, containerId, customerCode, quantity, batch, locationAreaId, rowNo, columnNo
     * 绑定关系 + 添加库存
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody InventoryData inventoryData) {

        String type = "INSERT";
        if (inventoryMapper.selectById(inventoryData.getId()) != null) type = "UPDATE";
        try {
            // 操作日志
            LogUtil.success(
                    logMapper,
                    type,
                    "修改库存EDIT " + inventoryData
            );

            inventoryService.add(inventoryData);
            return Result.success();
        }catch (Exception e){

            // 操作日志
            LogUtil.fail(
                    logMapper,
                    type,
                    "添加失败",
                    "修改库存EDIT " + inventoryData
            );

            return Result.fail(e.getMessage());
        }
    }



    /**
     * 获取详细数据
     */
    @GetMapping("/list/{containerId}")
    public Result<List<InventoryData>> getData(@PathVariable String containerId){
        return Result.success(inventoryService.getData(containerId));

    }

    /**
     * 删除库存数据和绑定表对应数据
     */
    @DeleteMapping("/{id}")
    public Result<String> delInventory(@PathVariable int id){
        Inventory in = inventoryMapper.selectById(id);

        // 日志
        LogUtil.success(
                logMapper,
                "DELETE",
                "删除库存DELETE " + in
        );

        inventoryService.del(id);
        return Result.success();
    }


}