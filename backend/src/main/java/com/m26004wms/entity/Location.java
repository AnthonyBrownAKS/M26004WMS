package com.m26004wms.entity;

import lombok.Data;

@Data
public class Location {

    private String locationId;

    private String area;

    private Integer rowNo;

    private Integer colNo;

    private Integer levelNo;

    /**
     * 状态：
     * FREE / OCCUPIED
     */
    private String status;

    private String containerId;

}
