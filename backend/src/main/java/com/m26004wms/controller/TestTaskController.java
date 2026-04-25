package com.m26004wms.controller;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.m26004wms.entity.Task;
import com.m26004wms.mapper.TaskMapper;
import com.m26004wms.queue.TaskQueue;
import com.m26004wms.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestTaskController {

    @Autowired
    private TaskQueue taskQueue;

    @Autowired
    private TaskService taskService;


    @Autowired
    private TaskMapper taskMapper;


    /**
     * 测试：手动创建任务并进入队列
     */
    @PostMapping("/task")
    public String createTask(@RequestParam String type) {

        Task task = new Task();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskType(type); // IN / OUT
        task.setStatus("CREATED");
        task.setCreateTime(LocalDateTime.now());

        // 1. 写入队列
        taskQueue.addTask(task);

        return "任务已进入队列：" + task.getTaskId();
    }

}