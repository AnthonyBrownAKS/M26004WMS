package com.m26004wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("material_container")
public class MaterialContainer {
    private String materialCode;
    private String containerId;
    private Integer quantity;
    private String batch;
    private LocalDateTime creationTime;

    @TableId("id")
    private Integer id;
}
