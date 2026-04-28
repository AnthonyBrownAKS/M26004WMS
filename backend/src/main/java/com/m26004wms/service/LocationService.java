package com.m26004wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.m26004wms.dto.LocationExcelDTO;
import com.m26004wms.entity.Location;

import java.util.List;

public interface LocationService extends IService<Location> {

    void importExcel(List<LocationExcelDTO> list);

    List<Location> exportAll();
}
