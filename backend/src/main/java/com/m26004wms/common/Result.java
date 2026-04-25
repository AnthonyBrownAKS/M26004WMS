package com.m26004wms.common;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private Boolean success;

    // 私有构造，强制使用静态方法创建
    private Result() {}

    // ================= 成功 =================

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        result.success = true;
        result.timestamp = LocalDateTime.now();
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = success();
        result.data = data;
        return result;
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = success(data);
        result.message = message;
        return result;
    }

    // ================= 失败 =================

    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.code = 500;
        result.message = "fail";
        result.success = false;
        result.timestamp = LocalDateTime.now();
        return result;
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = fail();
        result.message = message;
        return result;
    }

    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = fail(message);
        result.code = code;
        return result;
    }


}
