package com.m26004wms.common;

import com.m26004wms.entity.Logs;
import com.m26004wms.mapper.LogMapper;

public class LogUtil {

    public static void success(
            LogMapper logMapper,
            String type,
            Object param
    ) {

        Logs log = new Logs();

        log.setType(type);

        log.setResult("SUCCESS");

        log.setParam(String.valueOf(param));

        logMapper.insertControl(log);

    }

    public static void fail(
            LogMapper logMapper,
            String type,
            String reason,
            Object param
    ) {

        Logs log = new Logs();

        log.setType(type);

        log.setResult("FAIL: " + reason);

        log.setParam(String.valueOf(param));

        logMapper.insertControl(log);

    }

}
