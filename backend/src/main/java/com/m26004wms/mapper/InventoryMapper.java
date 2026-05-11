package com.m26004wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m26004wms.entity.Inventory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

}
