package com.m26004wms.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


/**
 * 接收传进来的Excel表
 */

@Data
public class MaterialExcelDTO {

    @ExcelProperty("id")
    private String id;

    @ExcelProperty("code")
    private String code;

    @ExcelProperty("shortCode")
    private String shortCode;

    @ExcelProperty("name")
    private String name;

    @ExcelProperty("spec")
    private String spec;

    @ExcelProperty("isUnique")
    private Boolean isUnique;

    @ExcelProperty("validityDate")
    private Integer validityDate;

    @ExcelProperty("abcType")
    private String abcType;

    @ExcelProperty("safetyStock")
    private Integer safetyStock;

    @ExcelProperty("creationTime")
    private String creationTime;
}
