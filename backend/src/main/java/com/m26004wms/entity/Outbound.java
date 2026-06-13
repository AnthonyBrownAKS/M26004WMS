package com.m26004wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("outbound")
public class Outbound {

    private String materialCode;
    private String containerId;
    private String batch;
    private String customerCode;
    private int quantity;
    private LocalDateTime outTime;
    private String materialName;
    private String spec;

    @TableId("id")
    private int id;
}
