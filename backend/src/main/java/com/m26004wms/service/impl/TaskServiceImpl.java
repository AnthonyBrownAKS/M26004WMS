package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.m26004wms.entity.*;
import com.m26004wms.mapper.*;
import com.m26004wms.queue.TaskQueue;
import com.m26004wms.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private TaskQueue taskQueue;

    /**
     * 创建入库任务
     */
    @Override
    public String createInboundTask(String materialId, int quantity) {
        // 接受任务
        Task task = new Task();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskType("IN");
        task.setStatus("CREATED");
        task.setMaterialId(materialId);
        task.setCreateTime(LocalDateTime.now());

        // 任务进入队列
        taskQueue.addTask(task);

        taskMapper.insert(task);
        return task.getTaskId();
    }

    /**
     * 创建出库任务
     */
    @Override
    public String createOutboundTask(String containerId) {
        // 接受任务
        Task task = new Task();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskType("OUT");
        task.setStatus("CREATED");
        task.setContainerId(containerId);
        task.setCreateTime(LocalDateTime.now());

        // 任务进入队列
        taskQueue.addTask(task);

        taskMapper.insert(task);
        return task.getTaskId();
    }


    /**
     * 任务队列
     * 事务处理
     */
    @Transactional
    @Override
    public void executeSingleTask(Task task) {

        task.setExecuteTime(LocalDateTime.now());
        taskMapper.updateById(task);

        if ("IN".equals(task.getTaskType())) {
            handleInbound(task);
        } else {
            handleOutbound(task);
        }

    }


    @Override
    public Task getById(String taskId) {
        return taskMapper.selectById(taskId);
    }


    @Override
    public List<Task> list() {
        return taskMapper.selectList(null);
    }


    @Override
    public IPage<Task> pageTasks(int current, int size) {
        Page<Task> page = new Page<>(current, size);
        return taskMapper.selectPage(page, null);
    }

    /**
     * 入库处理
     * 事务处理
     */
    @Transactional
    public void handleInbound(Task task) {

        // 1. 找空库位
        Location location = locationMapper.selectEmptyLocationForUpdate();

        // 2. 找空容器
        Container container = containerMapper.selectEmptyContainerForUpdate();

        // ERROR:异常处理
        if (container == null || location == null) {
            failTask(task, "无可用容器或库位");
            return;
        }

        // 3. 绑定资源
        // 状态锁
        container.setMaterialId(task.getMaterialId());
        container.setStatus("LOCKED");
        container.setLocationId(location.getLocationId());
        containerMapper.updateById(container);

        location.setStatus("OCCUPIED");
        location.setContainerId(container.getContainerId());
        locationMapper.updateById(location);


        // 4.生成设备执行指令


        // 5.发送给设备控制模块


        // 6.任务状态=RUNNING
        task.setStatus("RUNNING");

        // 7.等待执行结果


        // 8.执行成功？


        // 9.更新库存/容器/库位状态
        container.setStatus("OCCUPIED");
        containerMapper.updateById(container);

        // 10.任务状态=Finished
        task.setContainerId(container.getContainerId());
        task.setTargetLocationId(location.getLocationId());
        finishTask(task, "入库完成");
    }

    /**
     * 出库处理
     * 事务处理
     */
    @Transactional
    public void handleOutbound(Task task) {

        // 1.查找目标容器
        Container container = containerMapper.selectById(task.getContainerId());

        if (container == null) {
            failTask(task, "容器不存在");
            return;
        }

        // 2.获取库位信息
        Location location = locationMapper.selectById(container.getLocationId());

        // 3.设备执行


        // 4.发送给设备控制模块


        // 5.任务状态=RUNNING
        task.setStatus("FINISHED");

        // 6.等待执行结果


        // 7.执行成功？


        // 8.更新库存/容器/库位状态
        container.setMaterialId(null);
        container.setStatus("EMPTY");
        container.setLocationId(null);
        containerMapper.updateById(container);

        if (location != null) {
            location.setStatus("FREE");
            location.setContainerId(null);
            locationMapper.updateById(location);
        }

        // 9. 任务状态=Finished
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
