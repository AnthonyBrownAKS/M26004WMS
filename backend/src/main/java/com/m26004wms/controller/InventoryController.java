package com.m26004wms.controller;


import com.m26004wms.common.Result;
import com.m26004wms.entity.Inventory;
import com.m26004wms.entity.InventoryData;
import com.m26004wms.entity.Material;
import com.m26004wms.mapper.InventoryMapper;
import com.m26004wms.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryMapper inventoryMapper;
    private final InventoryService inventoryService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<Object> page(

            @RequestParam Integer current,

            @RequestParam Integer size,

            @RequestParam(required = false) String containerId
    ) {

        return Result.success(inventoryService.page(current, size, containerId));

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

        Map<String, Object> inventory =

                inventoryMapper.searchInventory(
                        materialCode,
                        batch
                );

        if (inventory == null) {

            return Result.fail(
                    "未找到库存"
            );
        }

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
        try {
            inventoryService.add(inventoryData);
            return Result.success();
        }catch (Exception e){
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
        inventoryService.del(id);
        return Result.success();
    }


}