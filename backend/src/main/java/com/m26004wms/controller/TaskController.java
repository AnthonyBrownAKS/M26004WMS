package com.m26004wms.controller;

import com.m26004wms.entity.Scan;
import com.m26004wms.entity.Task;
import com.m26004wms.service.TaskService;
import com.m26004wms.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * 创建入库任务
     * 传入 Scan 类
     *
     */
    @PostMapping("/inbound")
    public Result<String> createInbound(@RequestBody Scan scan) {
        String taskId = taskService.createInboundTask(scan);

        return Result.success("入库任务创建成功", taskId);
    }

    /**
     * 创建出库任务
     */
    @PostMapping("/outbound")
    public Result<String> createOutbound(@RequestParam String containerId) {
        String taskId = taskService.createOutboundTask(containerId);

        return Result.success("出库任务创建成功", taskId);
    }


    /**
     * 创建扫码任务
     */
    @GetMapping("/scan")
    public Result<Scan> scanQR(){

        return Result.success("扫码数据", new Scan());
    }



    /**
     * 查询任务详情
     */
    @GetMapping("/{taskId}")
    public Result<Task> getTask(@PathVariable String taskId) {
        Task task = taskService.getById(taskId);
        return Result.success("查询成功", task);
    }

    /**
     * 查询任务列表
     */
    @GetMapping("/list")
    public Result<List<Task>> list() {
        List<Task> list = taskService.list();
        return Result.success("查询成功", list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<Object> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {

        return Result.success("分页查询成功",
                taskService.pageTasks(current, size));
    }
}

