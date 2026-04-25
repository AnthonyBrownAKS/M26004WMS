package com.m26004wms.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Task {

    private String taskId;

    /**
     * IN / OUT
     */
    private String taskType;

    /**
     * CREATED / RUNNING / FINISHED / FAILED
     */
    private String status;

    private String containerId;

    private String materialId;

    private String sourceLocationId;

    private String targetLocationId;

    private LocalDateTime createTime;

    private LocalDateTime executeTime;

    private LocalDateTime finishTime;

}
