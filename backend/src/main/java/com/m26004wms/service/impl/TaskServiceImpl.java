package com.m26004wms.service.impl;

import com.m26004wms.common.LogUtil;
import com.m26004wms.common.Result;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private OutboundMapper outboundMapper;
    @Autowired
    private InboundMapper inboundMapper;
    @Autowired
    private LogMapper logMapper;


    @Override
    public String updateStatus(String taskId, String status){
        Task task = taskMapper.selectById(taskId);
        if (task == null) return null;
        task.setStatus(status);
        taskMapper.insertOrUpdate(task);
        return "SUCCESS";
    }

    /**
     * 绑定任务
     */
    @Transactional
    @Override
    public String createInboundTask(Scan scan) {
        // 任务记录操作内容
        LocalDateTime time = LocalDateTime.now();
        // material_container绑定物料与容器
        MaterialContainer mc = new MaterialContainer();

        mc.setMaterialCode(scan.getMaterialCode());
        mc.setContainerId(scan.getContainerId());
        mc.setQuantity(scan.getQuantity());
        mc.setCustomerCode(scan.getCustomerCode());

        // 批次定义
        mc.setBatch(time.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        mc.setCreationTime(time);
        materialContainerMapper.insertOrUpdate(mc);

        // 入库记录
        Material m = materialMapper.getByCustomerCode(mc.getMaterialCode());
        Inbound in = new Inbound();
        in.setMaterialName(m.getName());
        in.setBatch(mc.getBatch());
        in.setSpec(m.getSpec());
        in.setInboundTime(LocalDateTime.now());
        in.setQuantity(mc.getQuantity());
        in.setContainerId(mc.getContainerId());
        in.setCustomerCode(mc.getCustomerCode());

        inboundMapper.insertOrUpdate(in);

        return "ok";
    }


    /**
     * WCS入库接口1
     * 分配货位
     */
    @Override
    public Location getLocation(){

        LogUtil.success(
                logMapper,
                "SELECT",
                "货位获取"
        );

        return locationMapper.selectEmptyLocation();
    }

    /**
     * WCS入库接口1-2
     * 生成任务
     */
    @Override
    public String getTask(Location location, String code){


        Task task = new Task();
        task.setContainerId(code);
        task.setStatus("CREATED");
        task.setTaskType("INBOUND");
        task.setCreateTime(LocalDateTime.now());
        task.setTaskId(UUID.randomUUID().toString());
        task.setTargetLocationId(location.getId());
        taskMapper.insertOrUpdate(task);

        return task.getTaskId();
    }

    /**
     * WCS入库接口2
     * 完成入库
     */
    @Override
    public String inboundFinished(String taskId) {

        Task task = taskMapper.getTask(taskId);
        if (task == null) return "任务ID不存在: "+ taskId;

        Inventory inventory = new Inventory();

        Location location = locationMapper.selectById(task.getTargetLocationId());

        inventory.setLayerNo(location.getLayer());
        inventory.setColumnNo(location.getColumn());
        inventory.setRowNo(location.getRow());
        inventory.setContainerId(task.getContainerId());
        inventory.setLocationId(task.getTargetLocationId());
        inventory.setLocationAreaId("A");

        // 插入库存
        inventory.setCreationTime(LocalDateTime.now());
        inventoryMapper.insert(inventory);

        LogUtil.success(
                logMapper,
                "INSERT",
                "插入库存" + inventory
        );

        // 任务更新
        task.setStatus("FINISHED");
        task.setFinishTime(LocalDateTime.now());
        taskMapper.updateById(task);

        LogUtil.success(
                logMapper,
                "UPDATE",
                "任务更新" + inventory
        );

        // 库位表更新
        location.setCargoStatus("FULL");
        location.setLockState("INBOUND_LOCK");
        location.setLockContainerBarcode(inventory.getContainerId());
        locationMapper.updateById(location);

        // 异常?

        return "入库完成!";
    }


    /**
     * 创建出库任务
     */
    @Override
    public String createOutboundTask(String containerId) {
        // 重复出库异常？

        // 接受任务
        Task task = new Task();

        // 设置原位置
        task.setTargetLocationId(inventoryMapper.getLocationAreaIdByContainerId(containerId));

        String id = UUID.randomUUID().toString();
        task.setTaskId(id);
        task.setTaskType("OUTBOUND");
        task.setStatus("CREATED");
        task.setContainerId(containerId);
        task.setCreateTime(LocalDateTime.now());

        // 任务进入队列
        taskMapper.insert(task);
        return id;
    }


    // 页面出库
    @Override
    public String outboundMC(MaterialContainer materialContainer) {
        if (materialContainerMapper.selectByContainerId(materialContainer.getContainerId()) == null) return "库位信息不存在";

        // 添加出库任务记录
        Outbound ob = new Outbound();
        ob.setMaterialCode(materialContainer.getMaterialCode());
        ob.setContainerId(materialContainer.getContainerId());
        ob.setQuantity(materialContainer.getQuantity());
        ob.setBatch(materialContainer.getBatch());
        ob.setCustomerCode(materialContainer.getCustomerCode());
        ob.setOutTime(LocalDateTime.now());

        Material m = materialMapper.getByCustomerCode(materialContainer.getMaterialCode());
        ob.setSpec(m.getSpec());
        ob.setMaterialName(m.getName());

        outboundMapper.insert(ob);

        materialContainerMapper.deleteByContainerId(materialContainer.getContainerId());
        return "解绑成功";
    }

    /**
     * WCS出库接口1
     * 获取最近出库任务
     */
    @Override
    public Task getOutboundTask() {

        // 获取最早的status为Created的OUTBOUND任务
        return taskMapper.getEarliestCreatedTask();
    }

    /**
     * WCS出库接口2
     * 完成出库任务
     */
    @Override
    public String finishedOutbound(String taskId, String status) {
        Task task = taskMapper.getTask(taskId);
        if (task == null) return null;

        task.setStatus("FINISHED");
        task.setFinishTime(LocalDateTime.now());
        taskMapper.updateById(task);

        // 解绑库位与容器
        inventoryMapper.deleteByContainerId(task.getContainerId());

        return "出库任务完成";
    }


    /**
     * 扫码任务
     */
    @Override
    public Scan ScanQRTask() {

        Scan scan = new Scan();
        MaterialContainer mc = materialContainerMapper.selectById(1);
        // ======WCS流程,扫码获取数据======
        // scan:materialCode, quantity, containerId, customerCode
        scan.setMaterialCode(mc.getMaterialCode());
        scan.setQuantity(mc.getQuantity());
        scan.setContainerId(mc.getContainerId());
        scan.setCustomerCode(mc.getCustomerCode());

        // ======WCS流程=================
        return scan;
    }

    /**
     * 扫码完成出库
     */
    @Override
    public String scanOutbound(Scan scan) {
        // 解除绑定
        materialContainerMapper.deleteByContainerId(scan.getContainerId());


        return "扫码出库成功";
    }

    /**
     * 任务队列
     * 事务处理
     */
    @Transactional
    @Override
    public void executeSingleTask(Task task) {

        // ======线程池闲置中======

        // task.setExecuteTime(LocalDateTime.now());
        // taskMapper.updateById(task);

        // if ("IN".equals(task.getTaskType())) {
        //     handleInbound(task);
        // } else {
        //     handleOutbound(task);
        // }
    }


    @Override
    public Task getById(String taskId) {
        return taskMapper.selectById(taskId);
    }


    @Override
    public List<Task> list() {
        return taskMapper.selectList(null);
    }

    // 分页
    @Override
    public Object pageTasks(

            int current,

            int size,

            String searchContainerId

    ) {

        // 计算跳过多少条

        int offset =
                (current - 1) * size;

        // 查询数据

        List<Task> records =

                taskMapper.selectPage(
                        offset,
                        size,
                        searchContainerId
                );

        // 查询总数

        Long total =

                taskMapper.selectCount(
                        searchContainerId
                );

        // 总页数

        long pages = (total + size - 1) / size;

        // 返回结构

        Map<String, Object> result =
                new HashMap<>();

        result.put("records", records);

        result.put("current", current);

        result.put("pages", pages);

        result.put("size", size);

        result.put("total", total);

        return result;
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
