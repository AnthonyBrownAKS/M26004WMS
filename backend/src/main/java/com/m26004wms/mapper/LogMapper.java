package com.m26004wms.mapper;

import com.m26004wms.entity.Logs;
import com.m26004wms.entity.Material;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LogMapper {


    /**
     * 分页
     */
    @Select("""

            SELECT
                *
            FROM
                wcs_log
            WHERE
                creation_time >= #{startTime}
                AND
                creation_time <= #{endTime}
            ORDER BY
                creation_time DESC
            
            limit #{offset}, #{size}

            """)
    List<Logs> selectPage(
            @Param("offset")
            int offset,

            @Param("size")
            int size,

            @Param("startTime")
            String startTime,

            @Param("endTime")
            String endTime
    );


    @Select("""

            SELECT
                COUNT(*)
            FROM
                wcs_log
            WHERE
                creation_time >= #{startTime}
                AND
                creation_time <= #{endTime}

            """)
    Long selectCount(
            @Param("startTime")
            String startTime,

            @Param("endTime")
            String endTime
    );



}
