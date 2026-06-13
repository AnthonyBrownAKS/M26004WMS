package com.m26004wms.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.m26004wms.config.BooleanToStringConverter;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("container")
public class Container {

    @TableId("id")
    private String id;

    private String warehouseId;

    private String code;

    private String barcode;

    private String containerTypeId;

    private String loadState;

    private String locationId;

    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isStack;

    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isDeleted;

    private String deleterId;

    private LocalDateTime deletionTime;

    private LocalDateTime lastModificationTime;

    private String lastModifierId;

    private LocalDateTime creationTime;

    private String creatorId;

    private String extraProperties;

    private String concurrencyStamp;
}
