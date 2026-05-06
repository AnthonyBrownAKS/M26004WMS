package com.m26004wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inventory")
public class Inventory {
    @TableId("id")
    private Integer id;

    private String locationAreaId;
    private Integer rowNo;
    private Integer columnNo;
    private String containerId;
    private LocalDateTime creationTime;
}
