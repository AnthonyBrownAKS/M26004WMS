package com.m26004wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m26004wms.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 指定container_id 寻找存在 status 为 CREATED 的任务
     */
    @Select("""
        SELECT COUNT(*) 
        FROM task
        WHERE container_id = #{containerId}
        AND status = 'CREATED'
    """)
    Long existsCreatedTask(@Param("containerId") String containerId);

    /**
     * 获取指定containerId的入库任务
     */
    @Select("""
        select *
        from task
        where status = 'CREATED'
        and container_id = #{containerId}
        limit 1
    """)
    Task selectCreatedTaskByContainerId(String containerId);

    /**
     * 获取 create_time 最早的 CREATED 任务
     */
    @Select("""
        SELECT *
        FROM task
        WHERE status = 'CREATED'
        ORDER BY create_time ASC
        LIMIT 1
    """)
    Task getEarliestCreatedTask();
}
