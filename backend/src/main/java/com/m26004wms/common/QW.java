package com.m26004wms.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;


/**
 * QueryWrapper简易封装
 */

public class QW {

    public static <T> LambdaQueryWrapper<T> eq(LambdaQueryWrapper<T> qw, Object val, SFunction<T, ?> col) {
        if (val != null && !(val instanceof String && ((String) val).trim().isEmpty())) {
            qw.eq(col, val);
        }
        return qw;
    }

    public static <T> LambdaQueryWrapper<T> like(LambdaQueryWrapper<T> qw, String val, SFunction<T, ?> col) {
        if (val != null && !val.trim().isEmpty()) {
            qw.like(col, val);
        }
        return qw;
    }
}
