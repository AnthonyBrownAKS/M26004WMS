package com.m26004wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m26004wms.entity.Inventory;
import com.m26004wms.entity.MaterialContainer;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface InventoryMapper extends BaseMapper<Inventory> {

    /**
     * 根据 containerId 和 locationAreaId 删除库存数据
     */
    @Delete("""
        DELETE FROM inventory
        WHERE container_id = #{containerId}
        AND location_area_id = #{locationAreaId}
    """)
    int deleteByContainerIdAndArea(@Param("containerId") String containerId,
                                   @Param("locationAreaId") String locationAreaId);

    /**
     * 根据 container_id 删除数据
     */
    @Delete("""
        DELETE FROM inventory
        WHERE container_id = #{containerId}
    """)
    int deleteByContainerId(@Param("containerId") String containerId);

    @Select("""
        SELECT location_id
        FROM inventory
        WHERE container_id = #{containerId}
    """)
    String getLocationAreaIdByContainerId(@Param("containerId") String containerId);


    @Select("""
        SELECT *
        FROM inventory
        WHERE container_id = #{containerId}
    """)
    List<Inventory> exists(@Param("containerId") String containerId);


    /**
     * 分页查询
     */
    @Select("""

            select *

            from inventory
            
            where

            (
     
                container_id like concat('%', #{containerId}, '%') AND
                location_area_id like concat('%', #{locationAreaId}, '%')
            )

            order by creation_time desc

            limit #{offset}, #{size}

            """)
    List<Inventory> selectPage(
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("containerId") String containerId,
            @Param("locationAreaId") String locationAreaId
    );

    /**
     * 查询总数
     */
    @Select("""

            select count(*)

            from inventory
            
            where

            (
                #{containerId} is null
                or
                #{containerId} = ''
                or
                container_id like concat('%', #{containerId}, '%')
            )

            """)
    int selectCount(@Param("containerId") String containerId);


    /**
     * 根据物料+批次查询库存
     */
    @Select("""

            select

                customer_code as customerCode,

                container_id as containerId,

                quantity

            from material_container

            where material_code = #{materialCode}

            and batch = #{batch}

            limit 1

            """)
    Map<String, Object> searchInventory(

            @Param("materialCode")
            String materialCode,

            @Param("batch")
            String batch

    );


    /**
     * 通过containerId获取所有物料信息
     */
    @Select("""
        SELECT *
        FROM material_container
        WHERE container_id = #{containerId}
    """)
    List<MaterialContainer> getData(@Param("containerId") String containerId);


    /**
     * 检查是否有重复容器
     */
    @Select("""
        SELECT *
        FROM inventory
        WHERE container_id = #{containerId}
    """)
    List<Inventory> exitContainer(@Param("containerId") String containerId);


}
