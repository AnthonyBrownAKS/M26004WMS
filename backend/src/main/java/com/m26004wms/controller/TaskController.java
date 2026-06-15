package com.m26004wms.controller;

import com.m26004wms.common.LogUtil;
import com.m26004wms.entity.*;
import com.m26004wms.mapper.InventoryMapper;
import com.m26004wms.mapper.LocationMapper;
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

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    /**
     * 绑定程序√
     * 传入 Scan 类 materialCode, quantity, containerId, customerCode
     */
    @PostMapping("/inbound")
    public Result<String> createInbound(@RequestBody Scan scan) {
        String message = taskService.createInboundTask(scan);

        Logs log = new Logs();
        log.setType("INSERT");
        log.setParam("创建入库任务INSERT " + scan);

        if (message == null) {
            log.setResult("FAIL " + "入库失败");
            return Result.fail("入库失败");
        }
        logMapper.insertControl(log);

        return Result.success(message);
    }

    /**
     * WCS接口0
     * 更新状态
     * 任务与任务状态
     */
    @PostMapping("/status")
    public Result<String> updateStatus(@RequestBody Task task){
        String res = taskService.updateStatus(task.getTaskId(), task.getStatus());
        if (res == null) return Result.fail("任务不存在");
        return Result.success(res);
    }

    /**
     * WCS接口1
     * 分配库位√
     * code:PT0012
     */
    @GetMapping("/getLocation")
    public Result<LocationRequest> getLocation(@RequestParam String code){
        // 检查是否有重复容器
        if (!inventoryMapper.exists(code).isEmpty()) return Result.fail("容器已存在");

        Location location = taskService.getLocation();
        String taskId = taskService.getTask(location, code);
        if (location == null) return Result.fail("货位没有空位!");

        // WCS日志
        Logs log = new Logs();
        log.setType("INBOUND");
        log.setParam("获取库位信息\n" + code);
        log.setResult("SUCCESS 库位ID:" + location.getId());
        logMapper.insertWcs(log);

        // 若为空则返回null
        return Result.success(new LocationRequest(location, taskId));
    }

    /**
     * WCS接口2
     * 完成入库
     * 传入值命名规范: TaskId
     */
    @PostMapping("/finishedInbound")
    public Result<String> taskFinished(@RequestParam String taskId){

        // WCS日志
        Logs log = new Logs();
        log.setType("INBOUND");
        log.setParam("完成入库");
        log.setResult("SUCCESS 任务ID:" + taskId);
        logMapper.insertWcs(log);

        return Result.success(taskService.inboundFinished(taskId));
    }

    /**
     * 创建出库任务
     * 传入值: materialCode, batch, containerId, customerCode, quantity
     */
    @PostMapping("/createOutbound/{containerId}")
    public Result<String> createOutbound(@PathVariable String containerId) {
        String message = taskService.createOutboundTask(containerId);

        return Result.success(message);
    }

    /**
     * 解绑容器物料
     * 传入值: materialCode, batch, containerId, customerCode, quantity
     */
    @PostMapping("/outbound")
    public Result<String> outbound(@RequestBody MaterialContainer mc) {
        String message = taskService.outboundMC(mc);
        return Result.success(message);
    }


    /**
     * WCS接口1
     * 获取出库任务
     */
    @GetMapping("/getTask")
    public Result<OutboundRequest> getTask(){
        Task task = taskService.getOutboundTask();
        if (task == null) return Result.fail("没有出库任务");

        // WCS日志
        Logs log = new Logs();
        log.setType("OUTBOUND");
        log.setParam("获取到出库任务");
        log.setResult("SUCCESS 任务" + task);
        logMapper.insertWcs(log);

        // 返回处理
        Location location = locationMapper.selectById(task.getTargetLocationId());
        if(location == null) return Result.success(null);

        OutboundRequest or = new OutboundRequest(task, location.getRow(), location.getColumn(), location.getLayer());
        return Result.success(or);
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
        log.setParam("出库任务完成\nTASK( id: " + id + " ; status: " + status + " )");
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
                    "搜索任务SELECT MATERIAL BY CONTAINER_ID( " + containerId + " )"
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
                    "删除任务DELETE " + taskMapper.selectById(id)
            );

            taskMapper.deleteById(id);
            return Result.success();
        }catch(Exception e) {
            LogUtil.fail(
                    logMapper,
                    "DELETE",
                    "删除失败",
                    "删除任务DELETE " + taskMapper.selectById(id)
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
                "修改任务EDIT " + task
        );

        if (task.getCreateTime() == null) task.setCreateTime(LocalDateTime.now());
        if (task.getStatus().equals("FINISHED")) task.setFinishTime(LocalDateTime.now());

        taskMapper.insertOrUpdate(task);
        return Result.success();
    }


}

