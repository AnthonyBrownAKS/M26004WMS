package com.m26004wms.service.impl;

import com.m26004wms.dto.WcsLogChartVO;
import com.m26004wms.dto.WcsLogCountDTO;
import com.m26004wms.entity.Logs;
import com.m26004wms.mapper.LogMapper;
import com.m26004wms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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


    @Override
    public Object pageControl(Integer current, Integer size, String startTime, String endTime) {

        int offset = (current - 1) * size;

        if (startTime == null || startTime.isBlank()) {
            startTime = "2000-01-01 00:00:00";
        }
        if (endTime == null || endTime.isBlank()) {
            endTime = "2099-12-31 23:59:59";
        }

        List<Logs> records = logMapper.selectPageControl(offset, size, startTime, endTime);

        Long total = logMapper.selectCountControl(startTime, endTime);

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



    @Override
    public WcsLogChartVO getChartData() {

        List<WcsLogCountDTO> list =
                logMapper.getChartData();

        // 日期去重排序

        Set<String> dateSet = new LinkedHashSet<>();

        for (WcsLogCountDTO dto : list) {

            dateSet.add(dto.getLogDate());
        }

        List<String> dates = new ArrayList<>(dateSet);

        // 初始化数据

        Map<String,Integer> inboundMap =
                new HashMap<>();

        Map<String,Integer> outboundMap =
                new HashMap<>();

        for (WcsLogCountDTO dto : list) {

            if ("INBOUND".equals(dto.getType())) {

                inboundMap.put(
                        dto.getLogDate(),
                        dto.getCount()
                );
            }

            if ("OUTBOUND".equals(dto.getType())) {

                outboundMap.put(
                        dto.getLogDate(),
                        dto.getCount()
                );
            }
        }

        List<Integer> inbound = new ArrayList<>();

        List<Integer> outbound = new ArrayList<>();

        for (String date : dates) {

            inbound.add(
                    inboundMap.getOrDefault(date,0)
            );

            outbound.add(
                    outboundMap.getOrDefault(date,0)
            );
        }

        WcsLogChartVO vo =
                new WcsLogChartVO();

        vo.setDates(dates);

        vo.setInbound(inbound);

        vo.setOutbound(outbound);

        return vo;
    }

}
