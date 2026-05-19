package com.m26004wms.controller;

import com.m26004wms.common.Result;
import com.m26004wms.dto.WcsLogChartVO;
import com.m26004wms.mapper.LogMapper;
import com.m26004wms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
public class LogsController {

    @Autowired
    private LogService logService;

    @Autowired
    private LogMapper logMapper;

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


    // 图表数据
    @GetMapping("/chart")
    public Result<WcsLogChartVO> getChartData(){

        return Result.success(logService.getChartData());
    }

    // 清空日志
    @DeleteMapping("/clear")
    public Result<String> clearLogs(){

        logMapper.clearLogs();
        logMapper.clearLogsC();
        return Result.success("清空成功");
    }

    // 获取内存
    @GetMapping("/size")
    public Result<String> getLogSize(){

        Double size = logMapper.getSize() + logMapper.getSizeC();

        return Result.success(size + " MB");
    }


}
