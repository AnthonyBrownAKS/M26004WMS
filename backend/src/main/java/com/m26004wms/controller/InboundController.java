package com.m26004wms.controller;

import com.m26004wms.common.Result;
import com.m26004wms.entity.Inbound;
import com.m26004wms.mapper.InboundMapper;
import com.m26004wms.service.InboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inbound")
public class InboundController {

    @Autowired
    private InboundService inboundService;

    @Autowired
    private InboundMapper inboundMapper;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<Object> pageQuery(

            @RequestParam Integer current,
            @RequestParam Integer size,

            @RequestParam(required = false) String materialCode,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) String containerId,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String customerCode

    ) {

        Object page =
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
     * 新增或修改
     */
    @PutMapping("/add")
    public Result<String> add(

            @RequestBody Inbound inbound

    ) {

        inboundMapper.insertOrUpdate(inbound);

        return Result.success();

    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        inboundMapper.deleteById(id);
        return Result.success();
    }

    /**
     * ID查询
     */
    @GetMapping("/{id}")
    public Result<Inbound> getById(@PathVariable Long id) {
        return Result.success(inboundMapper.selectById(id));
    }

}