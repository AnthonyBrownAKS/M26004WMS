package com.m26004wms.entity;

import lombok.Data;

@Data
public class MaterialRequest {
    private String id;
    private String code;
    private String spec;
    private String name;

    private String customerCode;
    private String containerId;
    private String batch;
    private int quantity;
}
