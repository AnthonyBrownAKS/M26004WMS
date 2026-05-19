package com.m26004wms.controller;

import com.m26004wms.common.LogUtil;
import com.m26004wms.common.Result;
import com.m26004wms.entity.Inbound;
import com.m26004wms.entity.Logs;
import com.m26004wms.mapper.InboundMapper;
import com.m26004wms.mapper.LogMapper;
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

    @Autowired
    private LogMapper logMapper;

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

        if (!materialCode.isEmpty()
                || !materialName.isEmpty()
                || !containerId.isEmpty()
                || !batch.isEmpty()
                || !customerCode.isEmpty()){
            LogUtil.success(
                    logMapper,
                    "SELECT",
                    "SELECT INBOUND BY MATERIAL_CODE( " + materialCode + " ) AND " +
                            "MATERIAL_NAME( " + materialName + " ) AND " +
                            "CONTAINER_ID( " + containerId + " ) AND " +
                            "BATCH( " + batch + " ) AND " +
                            "CUSTOMER( " + customerCode + " )"
            );
        }

        return Result.success(page);

    }

    /**
     * 新增或修改
     */
    @PutMapping("/add")
    public Result<String> add(@RequestBody Inbound inbound) {
        inboundMapper.insertOrUpdate(inbound);

        // 操作日志
        Logs log = new Logs();
        log.setType("INSERT");
        if (inboundMapper.selectById(inbound.getId()) != null) log.setType("UPDATE");
        log.setResult("SUCCESS");
        log.setParam("EDIT " + inbound);
        logMapper.insertControl(log);

        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        inboundMapper.deleteById(id);

        // 操作日志
        Logs log = new Logs();
        log.setType("DELETE");
        log.setResult("SUCCESS");
        log.setParam("DELETE DATA IN INBOUND( ID:" + id +" )");
        logMapper.insertControl(log);

        return Result.success();
    }

    /**
     * ID查询
     */
    @GetMapping("/{id}")
    public Result<Inbound> getById(@PathVariable Long id) {
        Inbound in = inboundMapper.selectById(id);

        // 操作日志
        Logs log = new Logs();
        log.setType("SELECT");
        log.setResult("SUCCESS");
        log.setParam("GET DATA BY ID FROM INBOUND( ID:" + id +" )");
        logMapper.insertControl(log);

        return Result.success(in);
    }

}