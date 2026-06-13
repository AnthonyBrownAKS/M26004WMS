package com.m26004wms.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ContainerExcelDTO {

    @ExcelProperty("Id")
    private String id;

    @ExcelProperty("WarehouseId")
    private String warehouseId;

    @ExcelProperty("Code")
    private String code;

    @ExcelProperty("Barcode")
    private String barcode;

    @ExcelProperty("ContainerTypeId")
    private String containerTypeId;

    @ExcelProperty("LoadState")
    private String loadState;

    @ExcelProperty("LocationId")
    private String locationId;

    @ExcelProperty("IsStack")
    private Boolean isStack;

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
}
