package com.m26004wms.controller;

import com.m26004wms.common.LogUtil;
import com.m26004wms.entity.*;
import com.m26004wms.mapper.LogMapper;
import com.m26004wms.mapper.TaskMapper;
import com.m26004wms.service.TaskService;
import com.m26004wms.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

        Logs log = new Logs();
        log.setType("INSERT");
        log.setParam("INSERT " + scan);

        if (message == null) {
            log.setResult("FAIL " + "入库失败");
            return Result.fail("入库失败");
        }
        logMapper.insertControl(log);

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
        log.setParam("INBOUND BY CODE( " + code + " )");
        log.setResult("SUCCESS " + location);
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
        log.setParam("INBOUND BY " + inventory);
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

        // WCS日志
        Logs log = new Logs();
        log.setType("DELETE");
        log.setParam("DELETE BY " + mc);
        log.setResult("SUCCESS");
        logMapper.insertControl(log);

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
        log.setParam("OUTBOUND TASK " + task);
        log.setResult("SUCCESS");
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
        log.setParam("OUTBOUND BY TASK( id: " + id + " ; status: " + status + " )");
        if (message == null) {
            log.setResult("FAIL " + "没有找到对应任务ID");
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

        if (!containerId.isEmpty()){
            // 日志
            LogUtil.success(
                    logMapper,
                    "SELECT",
                    "SELECT MATERIAL BY CONTAINER_ID( " + containerId + " )"
            );
        }

        return Result.success("分页查询成功",
                taskService.pageTasks(current, size, containerId));
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteTask(@PathVariable String id){
        try{
            LogUtil.success(
                    logMapper,
                    "DELETE",
                    "DELETE " + taskMapper.selectById(id)
            );

            taskMapper.deleteById(id);
            return Result.success();
        }catch(Exception e) {
            LogUtil.fail(
                    logMapper,
                    "DELETE",
                    "删除失败",
                    "DELETE " + taskMapper.selectById(id)
            );

            return Result.fail("数据库错误");
        }
    }

    @PostMapping("/add")
    public Result<String> addTask(@RequestBody Task task){

        // 日志
        String type = taskMapper.selectById(task.getTaskId()) == null ? "INSERT" : "UPDATE";
        LogUtil.success(
                logMapper,
                type,
                "EDIT " + task
        );

        task.setCreateTime(LocalDateTime.now());

        taskMapper.insertOrUpdate(task);
        return Result.success();
    }


}

