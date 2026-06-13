package com.m26004wms.service;

import com.m26004wms.dto.ContainerExcelDTO;
import com.m26004wms.entity.Container;

import java.util.List;

public interface ContainerService {






    void importExcel(List<ContainerExcelDTO> list);

    List<Container> exportAll();
}
