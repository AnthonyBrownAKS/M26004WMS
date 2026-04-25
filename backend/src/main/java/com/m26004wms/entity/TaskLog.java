package com.m26004wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("task_log")
public class TaskLog {

    @TableId("log_id")
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
