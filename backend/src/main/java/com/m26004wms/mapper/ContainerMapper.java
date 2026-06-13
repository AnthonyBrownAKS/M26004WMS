package com.m26004wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m26004wms.entity.Container;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ContainerMapper extends BaseMapper<Container> {

    // 悲观锁
    @Select("SELECT * FROM container WHERE status = 'EMPTY' LIMIT 1 FOR UPDATE")
    Container selectEmptyContainerForUpdate();

}
