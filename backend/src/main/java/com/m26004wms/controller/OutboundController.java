package com.m26004wms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.m26004wms.common.Result;
import com.m26004wms.entity.Outbound;
import com.m26004wms.service.OutboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/outbound")
public class OutboundController {

    @Autowired
    private
    OutboundService outboundService;

    /**
     * 分页条件查询
     */
    @GetMapping("/page")
    public Result<Page<Outbound>> page(
            @RequestParam Integer current,
            @RequestParam Integer size,

            @RequestParam(required = false) String materialCode,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) String containerId,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String customerCode
    ) {

        Page<Outbound> page = outboundService.pageQuery(
                current,
                size,
                materialCode,
                materialName,
                containerId,
                batch,
                customerCode
        );

        return Result.success(page);
    }

    /**
     * 根据id查询
     */
    @GetMapping("/{id}")
    public Result<Outbound> getById(@PathVariable Long id) {

        return Result.success(outboundService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<String> save(@RequestBody Outbound outbound) {

        outboundService.save(outbound);

        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<String> update(@RequestBody Outbound outbound) {

        outboundService.updateById(outbound);

        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {

        outboundService.removeById(id);

        return Result.success();
    }
}
