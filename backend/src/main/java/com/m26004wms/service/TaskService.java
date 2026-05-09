package com.m26004wms.service;

import com.m26004wms.entity.*;

import java.util.List;

public interface TaskService {

    /**
     * 绑定容器ID与物料
     */
    String createInboundTask(Scan scan);

    /**
     * WCS入库接口1
     * 分配货位
     */
    Location getLocation();

    /**
     * WCS入库接口2
     * 完成入库, 插入库存
     */
    String inboundFinished(Inventory inventory);

    /**
     * 创建出库任务
     */
    String createOutboundTask(MaterialContainer materialContainer);

    /**
     * WCS出库接口1
     * 获取当前出库任务
     */
    Task getOutboundTask();

    /**
     * WCS出库接口2
     * 出库完成
     */
    String finishedOutbound(String taskId, String status);


    /**
     * 创建扫码任务
     */
    Scan ScanQRTask();

    /**
     * 扫码入库
     */
    String scanOutbound(Scan scan);


    /**
     * 执行任务 (任务队列)
     */
    void executeSingleTask(Task task);

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

    /**
     * 入库处理
     * 事务处理
     */
    public Location handleInbound(Task task);



}
