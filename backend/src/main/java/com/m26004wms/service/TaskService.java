package com.m26004wms.service;

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
     * 执行任务（简单版调度器）
     */
    void executeTask();

}
