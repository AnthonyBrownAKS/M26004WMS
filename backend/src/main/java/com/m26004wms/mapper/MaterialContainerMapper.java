package com.m26004wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface MaterialContainerMapper extends BaseMapper<com.m26004wms.entity.MaterialContainer> {

    /**
     * 根据 container_id 删除数据
     */
    @Delete("""
        DELETE FROM material_container
        WHERE container_id = #{containerId}
    """)
    int deleteByContainerId(@Param("containerId") String containerId);
}
