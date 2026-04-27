package com.m26004wms.controller;

import com.alibaba.excel.EasyExcel;
import com.m26004wms.common.Result;
import com.m26004wms.dto.MaterialExcelDTO;
import com.m26004wms.entity.Material;
import com.m26004wms.service.MaterialService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/material")
public class MaterialController {

    private final MaterialService service;

    public MaterialController(MaterialService service) {
        this.service = service;
    }

    // =========================
    // 导入Excel
    // =========================
    @PostMapping("/import")
    public Result<String> importExcel(@RequestParam("file") MultipartFile file) {

        try {
            List<MaterialExcelDTO> list =
                    EasyExcel.read(file.getInputStream())
                            .head(MaterialExcelDTO.class)
                            .sheet()
                            .doReadSync();

            service.importExcel(list);

            return Result.success("导入成功");

        } catch (Exception e) {
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    // =========================
    // 导出Excel
    // =========================
    @GetMapping("/exportAll")
    public void export(HttpServletResponse response) throws Exception {

        List<Material> list = service.exportAll();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode("物料数据", "UTF-8")
                .replaceAll("\\+", "%20");

        response.setHeader("Content-disposition",
                "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), Material.class)
                .sheet("物料表")
                .doWrite(list);
    }
}
