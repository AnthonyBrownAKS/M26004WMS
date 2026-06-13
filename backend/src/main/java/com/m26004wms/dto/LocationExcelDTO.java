package com.m26004wms.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationExcelDTO {

    @ExcelProperty("Id")
    private String id;

    @ExcelProperty("WarehouseId")
    private String warehouseId;

    @ExcelProperty("LocationAreaId")
    private String locationAreaId;

    @ExcelProperty("Code")
    private String code;

    @ExcelProperty("ShortCode")
    private String shortCode;

    @ExcelProperty("IsVirtually")
    private Boolean isVirtually;

    @ExcelProperty("Direction")
    private String direction;

    @ExcelProperty("Row")
    private Integer row;

    @ExcelProperty("Column")
    private Integer column;

    @ExcelProperty("Layer")
    private Integer layer;

    @ExcelProperty("Depth")
    private Integer depth;

    @ExcelProperty("DepthNum")
    private Integer depthNum;

    @ExcelProperty("MaxFullPalletNum")
    private Integer maxFullPalletNum;

    @ExcelProperty("MaxEmptyPalletNum")
    private Integer maxEmptyPalletNum;

    @ExcelProperty("IsAllowReceipt")
    private Boolean isAllowReceipt;

    @ExcelProperty("IsAllowSelect")
    private Boolean isAllowSelect;

    @ExcelProperty("ReceiptMode")
    private String receiptMode;

    @ExcelProperty("InboundMode")
    private String inboundMode;

    @ExcelProperty("InboundTrigMode")
    private String inboundTrigMode;

    @ExcelProperty("IsWithPallet")
    private Boolean isWithPallet;

    @ExcelProperty("AllowInbound")
    private Boolean allowInbound;

    @ExcelProperty("AllowOutbound")
    private Boolean allowOutbound;

    @ExcelProperty("CargoStatus")
    private String cargoStatus;

    @ExcelProperty("LockState")
    private String lockState;

    @ExcelProperty("LockContainerBarcode")
    private String lockContainerBarcode;

    @ExcelProperty("IsStocktake")
    private Boolean isStocktake;

    @ExcelProperty("X")
    private BigDecimal x;

    @ExcelProperty("Y")
    private BigDecimal y;

    @ExcelProperty("Width")
    private BigDecimal width;

    @ExcelProperty("Height")
    private BigDecimal height;

    @ExcelProperty("IsDeleted")
    private Boolean isDeleted;

    @ExcelProperty("ExtraProperties")
    private String extraProperties;

    @ExcelProperty("ConcurrencyStamp")
    private String concurrencyStamp;
}
