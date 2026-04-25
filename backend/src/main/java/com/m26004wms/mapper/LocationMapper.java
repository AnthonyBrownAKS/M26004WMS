package com.m26004wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m26004wms.entity.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LocationMapper extends BaseMapper<Location> {

    // 悲观锁
    @Select("SELECT * FROM location WHERE status = 'FREE' LIMIT 1 FOR UPDATE")
    Location selectEmptyLocationForUpdate();
}
