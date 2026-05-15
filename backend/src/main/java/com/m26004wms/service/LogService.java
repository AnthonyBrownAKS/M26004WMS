package com.m26004wms.service;

import com.m26004wms.entity.Logs;

import java.util.List;

public interface LogService {

    Object page(Integer current, Integer size, String startTime, String endTime);
}
