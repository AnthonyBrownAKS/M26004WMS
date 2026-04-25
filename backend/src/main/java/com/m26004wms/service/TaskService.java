package com.m26004wms.service;

import com.m26004wms.entity.Task;

import java.util.List;

public interface TaskService {

    /**
     * 创建入库任务
     */
    String createInboundTask(String materialId, int quantity);

    /**
     * 创建出库任务
     */
    String createOutboundTask(String containerId);

    /**
     * 执行任务 (任务队列)
     */
    public void executeSingleTask(Task task);

    /**
     * 通过ID查询任务
     */
    Task getById(String taskId);

    /**
     * 人物列表
     */
    List<Task> list();

    /**
     * 分页显示
     */
    Object pageTasks(int current, int size);

}
