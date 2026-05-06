package com.m26004wms.service;

import com.m26004wms.dto.MaterialExcelDTO;
import com.m26004wms.entity.Material;

import java.util.List;

public interface MaterialService {

    // 通过id获取材料
    Material getMaterialById(String id);

    //


    void importExcel(List<MaterialExcelDTO> list);

    List<Material> exportAll();
}
