package com.m26004wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.m26004wms.dto.MaterialExcelDTO;
import com.m26004wms.entity.Material;

import java.util.List;

public interface MaterialService extends IService<Material> {

    Material getMaterialById(String id);

    void importExcel(List<MaterialExcelDTO> list);

    List<Material> exportAll();

    Object page(Integer current, Integer size, String code);
}
