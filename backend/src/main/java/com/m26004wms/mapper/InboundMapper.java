package com.m26004wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.m26004wms.entity.Inbound;
import com.m26004wms.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface InboundMapper extends BaseMapper<Inbound> {

    /**
     * 分页查询
     */
    @Select("""

        SELECT *

        FROM inbound

        WHERE material_code LIKE CONCAT('%', #{materialCode}, '%')

        AND material_name LIKE CONCAT('%', #{materialName}, '%')

        AND container_id LIKE CONCAT('%', #{containerId}, '%')

        AND batch LIKE CONCAT('%', #{batch}, '%')

        AND customer_code LIKE CONCAT('%', #{customerCode}, '%')

        ORDER BY inbound_time DESC

        LIMIT #{offset}, #{size}

        """)
    List<Inbound> selectPage(
            @Param("offset") Integer offset,
            @Param("size") Integer size,

            @Param("materialCode") String materialCode,
            @Param("materialName") String materialName,
            @Param("containerId") String containerId,
            @Param("batch") String batch,
            @Param("customerCode") String customerCode
    );


    /**
     * 统计总数
     */
    @Select("""

        SELECT count(*)

        FROM inbound

        WHERE material_code LIKE CONCAT('%', #{materialCode}, '%')

        AND material_name LIKE CONCAT('%', #{materialName}, '%')

        AND container_id LIKE CONCAT('%', #{containerId}, '%')

        AND batch LIKE CONCAT('%', #{batch}, '%')

        AND customer_code LIKE CONCAT('%', #{customerCode}, '%')

        """)
    Long selectCount(
            @Param("materialCode") String materialCode,
            @Param("materialName") String materialName,
            @Param("containerId") String containerId,
            @Param("batch") String batch,
            @Param("customerCode") String customerCode
    );




}
