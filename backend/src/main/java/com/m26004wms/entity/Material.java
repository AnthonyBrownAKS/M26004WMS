package com.m26004wms.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Material {

    private String materialId;

    private String materialCode;

    private String materialName;

    private LocalDateTime inTime;

    private LocalDateTime outTime;

    private String status;

}
