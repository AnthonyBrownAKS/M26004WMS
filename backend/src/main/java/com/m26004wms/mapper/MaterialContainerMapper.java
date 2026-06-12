package com.m26004wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m26004wms.entity.MaterialContainer;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MaterialContainerMapper extends BaseMapper<com.m26004wms.entity.MaterialContainer> {

    /**
     * 根据 container_id 删除数据
     */
    @Delete("""
        DELETE FROM material_container
        WHERE container_id = #{containerId}
    """)
    void deleteByContainerId(@Param("containerId") String containerId);


    /**
     * 根据 container_id 删除数据
     */
    @Delete("""
        DELETE FROM material_container
        WHERE container_id = #{containerId}
              AND material_code = #{materialCode}
    """)
    void deleteByBatch(@Param("containerId") String containerId, @Param("materialCode") String materialCode);


    @Select("""
        SELECT *
        FROM material_container
        WHERE container_id = #{containerId}
    """)
    List<MaterialContainer> selectByContainerId(@Param("containerId") String containerId);

    @Select("""
        SELECT *
        FROM material_container
        WHERE container_id = #{containerId}
        AND material_code = #{materialCode}
    """)
    MaterialContainer getBatch(@Param("materialCode") String materialCode, @Param("containerId") String containerId);



}
