package com.m26004wms.service.impl;

import com.m26004wms.entity.Logs;
import com.m26004wms.entity.Material;
import com.m26004wms.mapper.LogMapper;
import com.m26004wms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public Object page(Integer current, Integer size, String startTime, String endTime) {

        int offset = (current - 1) * size;

        if (startTime == null || startTime.isBlank()) {
            startTime = "2000-01-01 00:00:00";
        }
        if (endTime == null || endTime.isBlank()) {
            endTime = "2099-12-31 23:59:59";
        }

        List<Logs> records = logMapper.selectPage(offset, size, startTime, endTime);

        Long total = logMapper.selectCount(startTime, endTime);

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
