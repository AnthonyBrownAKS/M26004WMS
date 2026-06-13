package com.m26004wms.entity;

import lombok.Data;

@Data
public class Scan {
    private String materialCode;
    private Integer quantity;
    private String containerId;
    private String customerCode;
}
