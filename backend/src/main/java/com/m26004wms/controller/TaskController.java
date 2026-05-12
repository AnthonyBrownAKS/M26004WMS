package com.m26004wms.controller;

import com.m26004wms.entity.*;
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
     * 绑定程序√
     * 传入 Scan 类 materialCode, quantity, containerId, customerCode
     */
    @PostMapping("/inbound")
    public Result<String> createInbound(@RequestBody Scan scan) {

        String message = taskService.createInboundTask(scan);
        if (message == null) return Result.fail("入库失败");
        return Result.success(message);
    }

    /**
     * WCS接口1
     * 分配库位√
     */
    @GetMapping("/getLocation")
    public Result<Location> getLocation(){
        Location location = taskService.getLocation();
        if (location == null) return Result.fail("货位没有空位!");
        return Result.success(location);
    }

    /**
     * WCS接口2
     * 分配库位√
     * 传入值命名规范: locationAreaId, rowNo, columnNo, containerId, locationId
     */
    @PostMapping("/finishedInbound")
    public Result<String> taskFinished(@RequestBody Inventory inventory){
        return Result.success(taskService.inboundFinished(inventory));
    }

    /**
     * 创建出库任务
     * 传入值: materialCode, batch, containerId
     */
    @PostMapping("/outbound")
    public Result<String> createOutbound(@RequestBody MaterialContainer mc) {
        String message = taskService.createOutboundTask(mc);
        if (message == null) return Result.fail("存在入库任务或重复创建!");
        return Result.success(message);
    }


    /**
     * WCS接口1
     * 获取出库任务
     */
    @GetMapping("/getTask")
    public Result<Task> getTask(){
        Task task = taskService.getOutboundTask();
        return Result.success(task);
    }

    /**
     * WCS接口2
     * 出库完成
     * 传入参数: 任务id, status
     */
    @PostMapping("/finishedOutbound")
    public Result<String> finishedOutbound(@RequestParam String id, @RequestParam String status){
        String message = taskService.finishedOutbound(id, status);
        if (message == null) return Result.fail("任务ID无效");
        return Result.success(message);
    }

    /**
     * 创建扫码任务
     * 轮询扫码结果
     */
    @GetMapping("/scan")
    public Result<Scan> scanQR(){
        Scan scan = taskService.ScanQRTask();
        return Result.success(scan);
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
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String containerId) {

        return Result.success("分页查询成功",
                taskService.pageTasks(current, size, containerId));
    }


}

