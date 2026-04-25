package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.m26004wms.entity.*;
import com.m26004wms.mapper.*;
import com.m26004wms.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ContainerMapper containerMapper;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private TaskLogMapper taskLogMapper;

    /**
     * 创建入库任务
     */
    @Override
    public void createInboundTask(String materialId, int quantity) {

        Task task = new Task();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskType("IN");
        task.setStatus("CREATED");
        task.setMaterialId(materialId);
        task.setCreateTime(LocalDateTime.now());

        taskMapper.insert(task);
    }

    /**
     * 创建出库任务
     */
    @Override
    public void createOutboundTask(String containerId) {

        Task task = new Task();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskType("OUT");
        task.setStatus("CREATED");
        task.setContainerId(containerId);
        task.setCreateTime(LocalDateTime.now());

        taskMapper.insert(task);
    }

    /**
     * 简单任务执行器（模拟调度）
     */
    @Override
    public void executeTask() {

        // 查找待执行任务
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "CREATED");

        List<Task> tasks = taskMapper.selectList(wrapper);

        for (Task task : tasks) {

            task.setStatus("RUNNING");
            task.setExecuteTime(LocalDateTime.now());
            taskMapper.updateById(task);

            if ("IN".equals(task.getTaskType())) {
                handleInbound(task);
            } else {
                handleOutbound(task);
            }
        }
    }

    /**
     * 入库处理
     */
    private void handleInbound(Task task) {

        // 1. 找空容器
        QueryWrapper<Container> cWrapper = new QueryWrapper<>();
        cWrapper.eq("status", "EMPTY");
        Container container = containerMapper.selectOne(cWrapper);

        // 2. 找空库位
        QueryWrapper<Location> lWrapper = new QueryWrapper<>();
        lWrapper.eq("status", "FREE");
        Location location = locationMapper.selectOne(lWrapper);

        if (container == null || location == null) {
            failTask(task, "无可用容器或库位");
            return;
        }

        // 3. 绑定关系
        container.setMaterialId(task.getMaterialId());
        container.setStatus("OCCUPIED");
        container.setLocationId(location.getLocationId());
        containerMapper.updateById(container);

        location.setStatus("OCCUPIED");
        location.setContainerId(container.getContainerId());
        locationMapper.updateById(location);

        // 4. 更新任务
        task.setContainerId(container.getContainerId());
        task.setTargetLocationId(location.getLocationId());
        finishTask(task, "入库完成");
    }

    /**
     * 出库处理
     */
    private void handleOutbound(Task task) {

        Container container = containerMapper.selectById(task.getContainerId());

        if (container == null) {
            failTask(task, "容器不存在");
            return;
        }

        Location location = locationMapper.selectById(container.getLocationId());

        // 1. 清空容器
        container.setMaterialId(null);
        container.setStatus("EMPTY");
        container.setLocationId(null);
        containerMapper.updateById(container);

        // 2. 释放库位
        if (location != null) {
            location.setStatus("FREE");
            location.setContainerId(null);
            locationMapper.updateById(location);
        }

        // 3. 更新任务
        task.setSourceLocationId(location != null ? location.getLocationId() : null);
        finishTask(task, "出库完成");
    }

    /**
     * 任务成功
     */
    private void finishTask(Task task, String message) {
        task.setStatus("FINISHED");
        task.setFinishTime(LocalDateTime.now());
        taskMapper.updateById(task);

        log(task.getTaskId(), "FINISHED", message);
    }

    /**
     * 任务失败
     */
    private void failTask(Task task, String message) {
        task.setStatus("FAILED");
        task.setFinishTime(LocalDateTime.now());
        taskMapper.updateById(task);

        log(task.getTaskId(), "FAILED", message);
    }

    /**
     * 写日志
     */
    private void log(String taskId, String step, String message) {
        TaskLog log = new TaskLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setTaskId(taskId);
        log.setStep(step);
        log.setMessage(message);
        log.setTime(LocalDateTime.now());

        taskLogMapper.insert(log);
    }

}
