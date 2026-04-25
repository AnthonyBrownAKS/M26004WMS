package com.m26004wms.service;

import com.m26004wms.entity.Task;

public interface TaskService {

    /**
     * 创建入库任务
     */
    void createInboundTask(String materialId, int quantity);

    /**
     * 创建出库任务
     */
    void createOutboundTask(String containerId);

    /**
     * 执行任务 (任务队列)
     */
    public void executeSingleTask(Task task);

}
