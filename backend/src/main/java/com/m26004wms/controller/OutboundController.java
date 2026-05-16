package com.m26004wms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.m26004wms.common.Result;
import com.m26004wms.entity.Inbound;
import com.m26004wms.entity.MaterialContainer;
import com.m26004wms.entity.Outbound;
import com.m26004wms.mapper.MaterialContainerMapper;
import com.m26004wms.mapper.OutboundMapper;
import com.m26004wms.service.OutboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/outbound")
public class OutboundController {

    @Autowired
    private OutboundService outboundService;

    @Autowired
    private OutboundMapper outboundMapper;

    @Autowired
    private MaterialContainerMapper materialContainerMapper;

    /**
     * 分页条件查询
     */
    @GetMapping("/page")
    public Result<Object> page(
            @RequestParam Integer current,
            @RequestParam Integer size,

            @RequestParam(required = false) String materialCode,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) String containerId,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String customerCode
    ) {

        Object page =
                outboundService.pageQuery(

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

        return Result.success(outboundMapper.selectById(id));
    }

    /**
     * 新增或修改
     */
    @PutMapping("/add")
    public Result<String> add(@RequestBody Outbound outbound) {
        outboundMapper.insertOrUpdate(outbound);
        return Result.success();

    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        outboundMapper.deleteById(id);
        return Result.success();
    }

    @PostMapping("/batch")
    public Result<MaterialContainer> getBatch(@RequestBody MaterialContainer materialContainer){
        return Result.success(materialContainerMapper.getBatch(materialContainer.getMaterialCode(), materialContainer.getContainerId()));
    }

}
