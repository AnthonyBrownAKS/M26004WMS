package com.m26004wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inbound")
public class Inbound {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String materialCode;

    private String materialName;

    private String spec;

    private String containerId;

    private String batch;

    private String customerCode;

    private Integer quantity;

    private LocalDateTime inboundTime;
}