package com.m26004wms.dto;

import com.m26004wms.entity.Inventory;

import lombok.Data;

import java.util.List;

@Data
public class InventoryPageDTO {

    private List<Inventory> inventory;

    private Long current;

    private Long pages;

    private Long size;

    private Long total;
}
