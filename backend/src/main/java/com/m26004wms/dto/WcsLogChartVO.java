package com.m26004wms.dto;

import lombok.Data;

import java.util.List;

@Data
public class WcsLogChartVO {

    private List<String> dates;

    private List<Integer> inbound;

    private List<Integer> outbound;
}
