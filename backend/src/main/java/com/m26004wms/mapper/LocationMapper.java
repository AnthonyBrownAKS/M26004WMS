package com.m26004wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m26004wms.entity.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LocationMapper extends BaseMapper<Location> {

    // 悲观锁
    @Select("SELECT * FROM location WHERE status = 'FREE' LIMIT 1 FOR UPDATE")
    Location selectEmptyLocationForUpdate();

    @Select("SELECT * FROM location WHERE CargoStatus = 'EMPTY' LIMIT 1")
    Location selectEmptyLocation();

    @Select("SELECT * FROM location WHERE LocationAreaId = #{locationAreaId}")
    Location selectLocationByLocationAreaId(@Param("locationAreaId") String locationAreaId);

    @Select("""
            SELECT Id
            FROM location
            WHERE LocationAreaId = #{locationAreaId}
            AND `Row` = #{rowNo}
            AND `Column` = #{columnNo}
            """)
    String selectLocationId(
            @Param("locationAreaId") String locationAreaId,
            @Param("rowNo") int rowNo,
            @Param("columnNo") int columnNo
    );
}
