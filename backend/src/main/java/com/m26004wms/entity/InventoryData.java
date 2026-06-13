package com.m26004wms.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryData {

    private Integer id;
    private String locationId;
    private Integer sum;

    private String materialCode;
    private String materialName;
    private String spec;

    private String customerCode;
    private String containerId;

    private Integer quantity;
    private String batch;
    private String locationAreaId;

    private Integer rowNo;
    private Integer columnNo;

    private LocalDateTime creationTime;

}
