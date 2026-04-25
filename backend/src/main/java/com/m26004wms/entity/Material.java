package com.m26004wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("material")
public class Material {

    @TableId("material_id")
    private String materialId;

    private String materialCode;

    private String materialName;

    private LocalDateTime inTime;

    private LocalDateTime outTime;

    private String status;

}
