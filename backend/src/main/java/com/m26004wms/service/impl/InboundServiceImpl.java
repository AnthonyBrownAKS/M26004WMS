package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.m26004wms.entity.Inbound;
import com.m26004wms.mapper.InboundMapper;
import com.m26004wms.service.InboundService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class InboundServiceImpl
        extends ServiceImpl<InboundMapper, Inbound>
        implements InboundService {

    @Override
    public Page<Inbound> pageQuery(

            Integer current,

            Integer size,

            String materialCode,

            String materialName,

            String containerId,

            String batch,

            String customerCode

    ) {

        Page<Inbound> page =
                new Page<>(current, size);

        QueryWrapper<Inbound> queryWrapper =
                new QueryWrapper<>();

        // 物料编码

        queryWrapper.like(

                StringUtils.hasText(materialCode),

                "material_code",

                materialCode

        );

        // 材料名称

        queryWrapper.like(
                StringUtils.hasText(materialName),
                "material_name",
                materialName
        );

        // 物料名称

        queryWrapper.like(

                StringUtils.hasText(materialName),

                "material_name",

                materialName

        );

        // 容器ID

        queryWrapper.like(

                StringUtils.hasText(containerId),

                "container_id",

                containerId

        );

        // 批次

        queryWrapper.like(

                StringUtils.hasText(batch),

                "batch",

                batch

        );

        // 客商编码

        queryWrapper.like(

                StringUtils.hasText(customerCode),

                "customer_code",

                customerCode

        );

        // 时间倒序

        queryWrapper.orderByDesc(
                "inbound_time"
        );

        return baseMapper.selectPage(
                page,
                queryWrapper
        );

    }

}
