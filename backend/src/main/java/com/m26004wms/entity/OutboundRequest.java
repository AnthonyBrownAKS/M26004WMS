package com.m26004wms.entity;

import com.m26004wms.mapper.LocationMapper;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OutboundRequest {
    private Task task;

    private int row_no;

    private int column_no;

    private int layer_no;


    public OutboundRequest(Task task, int row_no, int column_no, int layer_no) {
        this.task = task;
        this.row_no = row_no;
        this.column_no = column_no;
        this.layer_no = layer_no;

    }
}

