package com.m26004wms.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CustomerSupplierExcelDTO {

    @ExcelProperty("WarehouseId")
    private String warehouseId;

    @ExcelProperty("Name")
    private String name;

    @ExcelProperty("Code")
    private String code;

    @ExcelProperty("Address")
    private String address;

    @ExcelProperty("Email")
    private String email;

    @ExcelProperty("Phone")
    private String phone;

    @ExcelProperty("IsCustomer")
    private Boolean isCustomer;

    @ExcelProperty("IsSupplier")
    private Boolean isSupplier;

    @ExcelProperty("IsOwner")
    private Boolean isOwner;

    @ExcelProperty("IsDeleted")
    private Boolean isDeleted;

    @ExcelProperty("DeleterId")
    private String deleterId;

    @ExcelProperty("DeletionTime")
    private String deletionTime;

    @ExcelProperty("LastModificationTime")
    private String lastModificationTime;

    @ExcelProperty("LastModifierId")
    private String lastModifierId;

    @ExcelProperty("CreationTime")
    private String creationTime;

    @ExcelProperty("CreatorId")
    private String creatorId;

    @ExcelProperty("ExtraProperties")
    private String extraProperties;

    @ExcelProperty("ConcurrencyStamp")
    private String concurrencyStamp;

    @ExcelProperty("Id")
    private String id;
}