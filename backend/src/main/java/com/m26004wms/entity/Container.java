package com.m26004wms.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("container")
public class Container {

    @TableId("container_id")
    private String ContainerId;

    private String MaterialId;

    private Integer quantity;

    private String locationId;

    /** * 状态： * EMPTY / OCCUPIED / LOCKED */
    private String status;

}
