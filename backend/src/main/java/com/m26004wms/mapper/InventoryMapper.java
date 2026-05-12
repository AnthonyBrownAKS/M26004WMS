package com.m26004wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m26004wms.entity.Inventory;
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
        SELECT location_area_id
        FROM inventory
        WHERE container_id = #{containerId}
        LIMIT 1
    """)
    String getLocationAreaIdByContainerId(@Param("containerId") String containerId);


    /**
     * 分页查询
     */
    @Select("""

            select *

            from inventory

            order by creation_time desc

            limit #{offset}, #{size}

            """)
    List<Inventory> selectPage(
            @Param("offset") Long offset,
            @Param("size") Long size
    );

    /**
     * 查询总数
     */
    @Select("""

            select count(*)

            from inventory

            """)
    Long selectCount();


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

}
