package com.m26004wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("location")
public class Location {

    @TableId("location_id")
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
