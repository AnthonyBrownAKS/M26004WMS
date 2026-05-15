package com.m26004wms.service;

import com.m26004wms.dto.CustomerSupplierExcelDTO;
import com.m26004wms.entity.CustomerSupplier;

import java.util.List;

public interface CustomerSupplierService {

    /**
     * 导入xlsx表
     */
    void importExcel(List<CustomerSupplierExcelDTO> list);

    /**
     * 导出xlsx表
     */
    List<CustomerSupplier> exportAll();

    /**
     * 分页查询
     */
    Object page(Integer current, Integer size, String code);

}
