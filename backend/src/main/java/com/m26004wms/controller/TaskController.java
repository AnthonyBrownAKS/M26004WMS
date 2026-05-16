package com.m26004wms.controller;

import com.m26004wms.entity.*;
import com.m26004wms.mapper.LogMapper;
import com.m26004wms.mapper.TaskMapper;
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

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private LogMapper logMapper;

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
    public Result<Location> getLocation(@RequestBody String code){
        System.out.println(code);
        Location location = taskService.getLocation();
        if (location == null) return Result.fail("货位没有空位!");

        // WCS日志
        Logs log = new Logs();
        log.setType("INBOUND");
        log.setParam("收到CODE: " + code);
        log.setResult("库位ID: " + location.getId());
        logMapper.insertWcs(log);

        return Result.success(location);
    }

    /**
     * WCS接口2
     * 分配库位√
     * 传入值命名规范: locationAreaId, rowNo, columnNo, containerId, locationId
     */
    @PostMapping("/finishedInbound")
    public Result<String> taskFinished(@RequestBody Inventory inventory){

        // WCS日志
        Logs log = new Logs();
        log.setType("INBOUND");
        log.setParam("库存ID: " + inventory.getId());
        log.setResult("SUCCESS");
        logMapper.insertWcs(log);

        return Result.success(taskService.inboundFinished(inventory));
    }

    /**
     * 创建出库任务
     * 传入值: materialCode, batch, containerId, customerCode, quantity
     */
    @PostMapping("/outbound")
    public Result<String> createOutbound(@RequestBody MaterialContainer mc) {
        String message = taskService.createOutboundTask(mc);
        return Result.success(message);
    }


    /**
     * WCS接口1
     * 获取出库任务
     */
    @GetMapping("/getTask")
    public Result<Task> getTask(){
        Task task = taskService.getOutboundTask();

        // WCS日志
        Logs log = new Logs();
        log.setType("OUTBOUND");
        log.setParam("NONE");
        log.setResult("任务ID: " + task.getTaskId());
        logMapper.insertWcs(log);

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

        // WCS日志
        Logs log = new Logs();
        log.setType("OUTBOUND");
        log.setParam("任务ID: " + id + " / " + "状态: " + status);

        if (message == null) {
            log.setResult("任务ID无效");
            return Result.fail();
        }

        log.setResult("SUCCESS");
        logMapper.insertWcs(log);
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

    @DeleteMapping("/{id}")
    public Result<String> deleteTask(@PathVariable String id){
        try{
            taskMapper.deleteById(id);
            return Result.success();
        }catch(Exception e) {
            return Result.fail("数据库错误");
        }
    }

    @PostMapping("/add")
    public Result<String> addTask(@RequestBody Task task){
        taskMapper.insertOrUpdate(task);
        return Result.success();
    }


}

