package com.m26004wms.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaterialExcelDTO {

    @ExcelProperty("Id")
    private String id;

    @ExcelProperty("Code")
    private String code;

    @ExcelProperty("ShortCode")
    private String shortCode;

    @ExcelProperty("Name")
    private String name;

    @ExcelProperty("Spec")
    private String spec;

    @ExcelProperty("IsUnique")
    private Boolean isUnique;

    @ExcelProperty("ValidityDate")
    private Integer validityDate;

    @ExcelProperty("AbcType")
    private String abcType;

    @ExcelProperty("SafetyStock")
    private Integer safetyStock;

    @ExcelProperty("CreationTime")
    private String creationTime;
}
