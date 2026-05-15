package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.m26004wms.entity.Inbound;
import com.m26004wms.mapper.InboundMapper;
import com.m26004wms.service.InboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InboundServiceImpl implements InboundService {

    @Autowired
    private InboundMapper inboundMapper;

    @Override
    public Object pageQuery(

            Integer current,

            Integer size,

            String materialCode,

            String materialName,

            String containerId,

            String batch,

            String customerCode

    ) {

        int offset = (current - 1) * size;

        // 防止空值
        materialCode = materialCode == null ? "" : materialCode;
        materialName = materialName == null ? "" : materialName;
        containerId = containerId == null ? "" : containerId;
        batch = batch == null ? "" : batch;
        customerCode = customerCode == null ? "" : customerCode;

        List<Inbound> records = inboundMapper.selectPage(
                offset,
                size,
                materialCode,
                materialName,
                containerId,
                batch,
                customerCode
        );

        Long total = inboundMapper.selectCount(
                materialCode,
                materialName,
                containerId,
                batch,
                customerCode);

        long pages = (total + size - 1) / size;

        Map<String, Object> result =
                new HashMap<>();

        result.put("records", records);

        result.put("current", current);

        result.put("pages", pages);

        result.put("size", size);

        result.put("total", total);

        return result;


    }

}
