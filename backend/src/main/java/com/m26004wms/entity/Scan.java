package com.m26004wms.entity;

import lombok.Data;

@Data
public class Scan {
    private String materialId;
    private Integer quantity;
    private String containerId;
    private String code;
}
