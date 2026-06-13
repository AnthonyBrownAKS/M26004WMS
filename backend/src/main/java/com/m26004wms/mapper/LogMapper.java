package com.m26004wms.mapper;

import com.m26004wms.dto.WcsLogCountDTO;
import com.m26004wms.entity.Logs;
import com.m26004wms.entity.Material;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
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



    @Insert("""

            INSERT INTO wcs_log(

                type,
                param,
                result,
                creation_time

            )

            VALUES(

                #{type},
                #{param},
                #{result},
                NOW()

            )

            """)
    void insertWcs(Logs apilog);



    /**
     * 分页
     */
    @Select("""

            SELECT
                *
            FROM
                control_log
            WHERE
                creation_time >= #{startTime}
                AND
                creation_time <= #{endTime}
            ORDER BY
                creation_time DESC
            
            limit #{offset}, #{size}

            """)
    List<Logs> selectPageControl(
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
                control_log
            WHERE
                creation_time >= #{startTime}
                AND
                creation_time <= #{endTime}

            """)
    Long selectCountControl(
            @Param("startTime")
            String startTime,

            @Param("endTime")
            String endTime
    );


    @Insert("""

            INSERT INTO control_log(

                type,
                param,
                result,
                creation_time

            )

            VALUES(

                #{type},
                #{param},
                #{result},
                NOW()

            )

            """)
    void insertControl(Logs apilog);



    // 查询每天日志数量
    @Select("""
            SELECT
                DATE(creation_time) as logDate,
                type,
                COUNT(*) as count
            FROM wcs_log
            WHERE type IN ('INBOUND','OUTBOUND')
            GROUP BY DATE(creation_time), type
            ORDER BY logDate
            """)
    List<WcsLogCountDTO> getChartData();

    // 清空日志
    @Delete("TRUNCATE TABLE wcs_log")
    void clearLogs();

    // 清空日志
    @Delete("TRUNCATE TABLE control_log")
    void clearLogsC();


    // 查询每天日志数量
    @Select("""
            SELECT
                    ROUND((data_length + index_length) / 1024 / 1024,2) AS sizeMB
                    FROM information_schema.TABLES
                    WHERE table_schema = DATABASE()
                    AND table_name='wcs_log'
            """)
    Double getSize();

    // 查询每天日志数量
    @Select("""
            SELECT
                    ROUND((data_length + index_length) / 1024 / 1024,2) AS sizeMB
                    FROM information_schema.TABLES
                    WHERE table_schema = DATABASE()
                    AND table_name='control_log'
            """)
    Double getSizeC();

}
