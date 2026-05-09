package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.m26004wms.entity.*;
import com.m26004wms.mapper.*;
import com.m26004wms.mapper.MaterialContainerMapper;
import com.m26004wms.queue.TaskQueue;
import com.m26004wms.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private MaterialContainerMapper materialContainerMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    /**
     * 绑定任务
     */
    @Override
    public String createInboundTask(Scan scan) {

        // 库存表提前占用
        Inventory inventory = new Inventory();
        inventory.setLocationAreaId("C");
        inventory.setRowNo(1);
        inventory.setColumnNo(1);
        inventory.setContainerId(scan.getContainerId());
        inventoryMapper.insertOrUpdate(inventory);

        // 接受任务
        Task task = new Task();

        // 初始化设置
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskType("IN");
        task.setStatus("CREATED");

        // 任务记录操作内容
        LocalDateTime time = LocalDateTime.now();

        task.setMaterialId(scan.getMaterialCode());
        task.setContainerId(scan.getContainerId());
        task.setCreateTime(time);

        // material_container绑定物料与容器
        MaterialContainer mc = new MaterialContainer();

        mc.setMaterialCode(scan.getMaterialCode());
        mc.setContainerId(scan.getContainerId());
        mc.setQuantity(scan.getQuantity());

        // 批次定义
        mc.setBatch(time.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        mc.setCreationTime(time);

        materialContainerMapper.insertOrUpdate(mc);

        // 任务进入队列
        // taskQueue.addTask(task);
        taskMapper.insert(task);

        return task.getTaskId();
    }


    /**
     * WCS入库接口1
     * 分配货位
     */
    @Override
    public Location getLocation(){
        return locationMapper.selectEmptyLocation();
    }

    /**
     * WCS入库接口2
     * 插入库存
     */
    @Override
    public String inboundFinished(Inventory inventory) {
        // 删除占位库存
        inventoryMapper.deleteByContainerIdAndArea(inventory.getContainerId(), "C");

        // 插入库存
        inventory.setCreationTime(LocalDateTime.now());
        inventoryMapper.insert(inventory);

        // 异常?

        return "入库完成!";
    }


    /**
     * 创建出库任务
     */
    @Override
    public String createOutboundTask(MaterialContainer materialContainer) {
        // 判断是否存在重复库存
        if (taskMapper.existsCreatedTask(materialContainer.getContainerId()) > 0){
            return "重复创建!";
        }

        // 接受任务
        Task task = new Task();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskType("OUT");
        task.setStatus("CREATED");
        task.setContainerId(materialContainer.getContainerId());
        task.setCreateTime(LocalDateTime.now());

        // 任务进入队列
        // taskQueue.addTask(task);
        taskMapper.insert(task);
        return "出库完成!";
    }

    /**
     * WCS出库接口1
     * 获取最近出库任务
     */
    @Override
    public Task getOutboundTask() {
        return taskMapper.getEarliestCreatedTask();
    }

    /**
     * WCS出库接口2
     * 完成出库任务
     */
    @Override
    public String finishedOutbound(String taskId, String status) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) return "不存在该任务";

        task.setStatus("FINISHED");
        // 删除库存表数据
        inventoryMapper.deleteByContainerId(task.getContainerId());
        return "出库任务完成";
    }


    /**
     * 扫码任务
     */
    @Override
    public Scan ScanQRTask() {

        // ======WCS流程,扫码获取数据======
        // scan:materialCode, quantity, containerId, customerCode



        // ======WCS流程=================
        return new Scan();
    }

    /**
     * 扫码完成入库
     */
    @Override
    public String scanOutbound(Scan scan) {

        // 解除绑定
        materialContainerMapper.deleteByContainerId(scan.getContainerId());
        return "扫码入库成功";
    }

    /**
     * 任务队列
     * 事务处理
     */
    @Transactional
    @Override
    public void executeSingleTask(Task task) {

        // ======线程池闲置中======

//        task.setExecuteTime(LocalDateTime.now());
//        taskMapper.updateById(task);
//
//        if ("IN".equals(task.getTaskType())) {
//            handleInbound(task);
//        } else {
//            handleOutbound(task);
//        }
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
    @Override
    public Location handleInbound(Task task) {
        // 尝试分配空库位
        Location location = locationMapper.selectEmptyLocation();

        // ====================WCS======================
        // 返回location_area_id, row_no, column_no给WCS层
        return location;
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


//        // 8.更新库存/容器/库位状态
//        container.setMaterialId(null);
//        container.setStatus("EMPTY");
//        container.setLocationId(null);
//        containerMapper.updateById(container);
//
//        if (location != null) {
//            location.setStatus("FREE");
//            location.setContainerId(null);
//            locationMapper.updateById(location);
//        }

        // 9. 任务状态=Finished
        task.setSourceLocationId(location != null ? location.getId() : null);
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
