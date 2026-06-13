package com.m26004wms.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Logs {

    int id;
    String type;
    String result;
    String param;
    LocalDateTime creationTime;



}
