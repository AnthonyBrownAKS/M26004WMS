package com.m26004wms.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.m26004wms.config.BooleanToStringConverter;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("material")
public class Material {

    @TableId("id")
    private String id;

    private String code;

    private String shortCode;

    private String name;

    private String spec;

    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isUnique;

    private Integer validityDate;

    private String abcType;

    private Integer safetyStock;

    private LocalDateTime creationTime;

}
