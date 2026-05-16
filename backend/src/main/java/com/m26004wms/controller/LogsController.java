package com.m26004wms.controller;

import com.m26004wms.common.Result;
import com.m26004wms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogsController {

    @Autowired
    private LogService logService;

    @GetMapping("/apiLog")
    public Result<Object> page(

            @RequestParam Integer current,

            @RequestParam Integer size,

            @RequestParam(required = false)
            String startTime,

            @RequestParam(required = false)
            String endTime
    ) {

        return Result.success(logService.page(current, size, startTime, endTime));

    }

    @GetMapping("/controlLog")
    public Result<Object> pageControl(

            @RequestParam Integer current,

            @RequestParam Integer size,

            @RequestParam(required = false)
            String startTime,

            @RequestParam(required = false)
            String endTime
    ) {

        return Result.success(logService.pageControl(current, size, startTime, endTime));

    }



}
