package com.m26004wms.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CustomerSupplierExcelDTO {

    @ExcelProperty("warehouseId")
    private String warehouseId;

    @ExcelProperty("name")
    private String name;

    @ExcelProperty("code")
    private String code;

    @ExcelProperty("address")
    private String address;

    @ExcelProperty("email")
    private String email;

    @ExcelProperty("phone")
    private String phone;

    @ExcelProperty("isCustomer")
    private Boolean isCustomer;

    @ExcelProperty("isSupplier")
    private Boolean isSupplier;

    @ExcelProperty("isOwner")
    private Boolean isOwner;

    @ExcelProperty("is_deleted")
    private Boolean isDeleted;

    @ExcelProperty("deleterId")
    private String deleterId;

    @ExcelProperty("deletionTime")
    private String deletionTime;

    @ExcelProperty("lastModificationTime")
    private String lastModificationTime;

    @ExcelProperty("lastModifierId")
    private String lastModifierId;

    @ExcelProperty("creationTime")
    private String creationTime;

    @ExcelProperty("creatorId")
    private String creatorId;

    @ExcelProperty("extraProperties")
    private String extraProperties;

    @ExcelProperty("concurrencyStamp")
    private String concurrencyStamp;

    @ExcelProperty("id")
    private String id;
}