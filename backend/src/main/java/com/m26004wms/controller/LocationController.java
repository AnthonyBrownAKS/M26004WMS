package com.m26004wms.controller;

import com.alibaba.excel.EasyExcel;
import com.m26004wms.common.Result;
import com.m26004wms.dto.LocationExcelDTO;
import com.m26004wms.dto.MaterialExcelDTO;
import com.m26004wms.entity.Location;
import com.m26004wms.entity.Material;
import com.m26004wms.service.LocationService;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Resource
    private LocationService service;

    // =========================
    // Excel导入
    // =========================
    @PostMapping("/import")
    public Result<String> importExcel(@RequestParam("file") MultipartFile file) {

        try {
            List<LocationExcelDTO> list =
                    EasyExcel.read(file.getInputStream())
                            .head(LocationExcelDTO.class)
                            .sheet()
                            .doReadSync();

            service.importExcel(list);

            return Result.success("导入成功");

        } catch (Exception e) {
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    // =========================
    // Excel导出
    // =========================
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        List<Location> list = service.exportAll();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode("货位数据", "UTF-8")
                .replaceAll("\\+", "%20");

        response.setHeader("Content-disposition",
                "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), Location.class)
                .sheet("货位数据")
                .doWrite(list);
    }
}
