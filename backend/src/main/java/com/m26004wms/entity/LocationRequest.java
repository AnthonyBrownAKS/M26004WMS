package com.m26004wms.entity;

import lombok.Data;

@Data
public class LocationRequest {
    private Location location;
    private String taskId;

    public LocationRequest(Location location, String taskId) {
        this.location = location;
        this.taskId = taskId;
    }
}
