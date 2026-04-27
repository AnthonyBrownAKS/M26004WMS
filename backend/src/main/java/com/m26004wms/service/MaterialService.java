package com.m26004wms.service;

import com.m26004wms.dto.MaterialExcelDTO;
import com.m26004wms.entity.Material;

import java.util.List;

public interface MaterialService {

    void importExcel(List<MaterialExcelDTO> list);

    public List<Material> exportAll();
}
