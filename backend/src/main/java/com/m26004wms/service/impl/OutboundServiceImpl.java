package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.m26004wms.entity.Outbound;
import com.m26004wms.mapper.OutboundMapper;
import com.m26004wms.service.OutboundService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OutboundServiceImpl
        extends ServiceImpl<OutboundMapper, Outbound>
        implements OutboundService {

    @Override
    public Page<Outbound> pageQuery(
            Integer current,
            Integer size,
            String materialCode,
            String materialName,
            String containerId,
            String batch,
            String customerCode
    ) {

        Page<Outbound> page = new Page<>(current, size);

        QueryWrapper<Outbound> queryWrapper = new QueryWrapper<>();

        // 条件筛选
        queryWrapper.like(
                StringUtils.hasText(materialCode),
                "material_code",
                materialCode
        );

        queryWrapper.like(
                StringUtils.hasText(materialName),
                "material_name",
                materialName
        );

        queryWrapper.like(
                StringUtils.hasText(containerId),
                "container_id",
                containerId
        );

        queryWrapper.like(
                StringUtils.hasText(batch),
                "batch",
                batch
        );

        queryWrapper.like(
                StringUtils.hasText(customerCode),
                "customer_code",
                customerCode
        );

        // 按时间倒序
        queryWrapper.orderByDesc("out_time");

        return baseMapper.selectPage(page, queryWrapper);
    }
}
