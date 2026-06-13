package com.m26004wms.controller;

import com.alibaba.excel.EasyExcel;
import com.m26004wms.common.Result;
import com.m26004wms.dto.ContainerExcelDTO;
import com.m26004wms.entity.Container;
import com.m26004wms.service.ContainerService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/container")
public class ContainerController {

    private final ContainerService service;

    public ContainerController(ContainerService service) {
        this.service = service;
    }

    // =========================
    // 导入Excel
    // =========================
    @PostMapping("/import")
    public Result<String> importExcel(@RequestParam("file") MultipartFile file) {

        try {
            List<ContainerExcelDTO> list =
                    EasyExcel.read(file.getInputStream())
                            .head(ContainerExcelDTO.class)
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

        List<Container> list = service.exportAll();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode("容器数据", "UTF-8")
                .replaceAll("\\+", "%20");

        response.setHeader("Content-disposition",
                "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), Container.class)
                .sheet("容器数据")
                .doWrite(list);
    }
}
