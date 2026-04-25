package com.m26004wms.entity;


import lombok.Data;

@Data
public class Container {

    private String ContainerId;

    private String MaterialId;

    private Integer quantity;

    private String locationId;

    /** * 状态： * EMPTY / OCCUPIED / LOCKED */
    private String status;

}
