package com.m26004wms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.m26004wms.common.Result;
import com.m26004wms.entity.Inbound;
import com.m26004wms.service.InboundService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/inbound")
public class InboundController {

    @Resource
    private InboundService inboundService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<Page<Inbound>> pageQuery(

            @RequestParam Integer current,
            @RequestParam Integer size,

            @RequestParam(required = false) String materialCode,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) String containerId,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String customerCode

    ) {

        Page<Inbound> page =
                inboundService.pageQuery(

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
     * 新增
     */
    @PostMapping("/add")
    public Result<String> add(

            @RequestBody Inbound inbound

    ) {

        inboundService.save(inbound);

        return Result.success();

    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result<String> update(

            @RequestBody Inbound inbound

    ) {

        inboundService.updateById(inbound);

        return Result.success();

    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(

            @PathVariable Long id

    ) {

        inboundService.removeById(id);

        return Result.success();

    }

    /**
     * ID查询
     */
    @GetMapping("/{id}")
    public Result<Inbound> getById(

            @PathVariable Long id

    ) {

        return Result.success(

                inboundService.getById(id)

        );

    }

}