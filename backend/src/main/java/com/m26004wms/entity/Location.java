package com.m26004wms.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.m26004wms.config.BooleanToStringConverter;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("location")
public class Location {

    @TableId("Id")
    private String id;

    @TableField("WarehouseId")
    private String warehouseId;

    @TableField("LocationAreaId")
    private String locationAreaId;

    @TableField("Code")
    private String code;

    @TableField("ShortCode")
    private String shortCode;

    @TableField("IsVirtually")
    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isVirtually;

    @TableField("Direction")
    private String direction;

    @TableField("`Row`")
    private Integer row;

    @TableField("`Column`")
    private Integer column;

    @TableField("Layer")
    private Integer layer;

    @TableField("Depth")
    private Integer depth;

    @TableField("DepthNum")
    private Integer depthNum;

    @TableField("MaxFullPalletNum")
    private Integer maxFullPalletNum;

    @TableField("MaxEmptyPalletNum")
    private Integer maxEmptyPalletNum;

    @TableField("IsAllowReceipt")
    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isAllowReceipt;

    @TableField("IsAllowSelect")
    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isAllowSelect;

    @TableField("ReceiptMode")
    private String receiptMode;

    @TableField("InboundMode")
    private String inboundMode;

    @TableField("InboundTrigMode")
    private String inboundTrigMode;

    @TableField("IsWithPallet")
    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isWithPallet;

    @TableField("AllowInbound")
    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean allowInbound;

    @TableField("AllowOutbound")
    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean allowOutbound;

    @TableField("CargoStatus")
    private String cargoStatus;

    @TableField("LockState")
    private String lockState;

    @TableField("LockContainerBarcode")
    private String lockContainerBarcode;

    @TableField("IsStocktake")
    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isStocktake;

    @TableField("X")
    private BigDecimal x;

    @TableField("Y")
    private BigDecimal y;

    @TableField("Width")
    private BigDecimal width;

    @TableField("Height")
    private BigDecimal height;

    @TableField("IsDeleted")
    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isDeleted;

    @TableField("ExtraProperties")
    private String extraProperties;

    @TableField("ConcurrencyStamp")
    private String concurrencyStamp;
}


