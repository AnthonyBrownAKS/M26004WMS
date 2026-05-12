package com.m26004wms.controller;


import com.m26004wms.common.Result;
import com.m26004wms.entity.Inventory;
import com.m26004wms.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryMapper inventoryMapper;

    @GetMapping("/pages")
    public Map<String, Object> pages(

            @RequestParam Long current,

            @RequestParam Long size

    ) {

        // 计算跳过多少数据

        Long offset =
                (current - 1) * size;

        // 分页数据

        List<Inventory> inventory =

                inventoryMapper.selectPage(
                        offset,
                        size
                );

        // 总数

        Long total =
                inventoryMapper.selectCount();

        // 总页数

        Long pages =
                (total + size - 1) / size;

        // 返回

        Map<String, Object> result =
                new HashMap<>();

        result.put("records", inventory);

        result.put("current", current);

        result.put("pages", pages);

        result.put("size", size);

        result.put("total", total);

        return result;
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
}