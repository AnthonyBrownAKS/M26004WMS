package com.m26004wms.controller;

import com.m26004wms.entity.Task;
import com.m26004wms.service.TaskService;
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
     */
    @PostMapping("/inbound")
    public Map<String, Object> createInbound(@RequestParam String materialId, @RequestParam int quantity) {
        String taskId = taskService.createInboundTask(materialId, quantity);

        return success("入库任务创建成功", taskId);
    }

    /**
     * 创建出库任务
     */
    @PostMapping("/outbound")
    public Map<String, Object> createOutbound(@RequestParam String containerId) {
        String taskId = taskService.createOutboundTask(containerId);

        return success("出库任务创建成功", taskId);
    }

    /**
     * 查询任务详情
     */
    @GetMapping("/{taskId}")
    public Map<String, Object> getTask(@PathVariable String taskId) {
        Task task = taskService.getById(taskId);
        return success("查询成功", task);
    }

    /**
     * 查询任务列表
     */
    @GetMapping("/list")
    public Map<String, Object> list() {
        List<Task> list = taskService.list();
        return success("查询成功", list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {

        return success("分页查询成功",
                taskService.pageTasks(current, size));
    }


    /**
     * 通用成功返回
     */
    private Map<String, Object> success(String msg, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", msg);
        map.put("data", data);
        return map;
    }

}

