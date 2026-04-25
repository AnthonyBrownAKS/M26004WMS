package com.m26004wms.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskLog {

    private String logId;

    private String taskId;

    /**
     * 执行步骤
     */
    private String step;

    /**
     * 描述信息
     */
    private String message;

    private LocalDateTime time;

}
