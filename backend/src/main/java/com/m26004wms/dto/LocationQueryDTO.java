package com.m26004wms.dto;

import lombok.Data;

@Data
public class LocationQueryDTO {
    private Long warehouseId;
    private String code;
    private String direction;
}
