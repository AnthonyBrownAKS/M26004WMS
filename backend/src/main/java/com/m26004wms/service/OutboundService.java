package com.m26004wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.m26004wms.entity.Outbound;

public interface OutboundService {

    Object pageQuery(
            Integer current,
            Integer size,
            String materialCode,
            String materialName,
            String containerId,
            String batch,
            String customerCode
    );
}