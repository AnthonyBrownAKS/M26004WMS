package com.m26004wms.controller;

import com.alibaba.excel.EasyExcel;
import com.m26004wms.common.Result;
import com.m26004wms.dto.MaterialExcelDTO;
import com.m26004wms.entity.Material;
import com.m26004wms.mapper.MaterialMapper;
import com.m26004wms.service.MaterialService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialMapper materialMapper;


    public MaterialController(MaterialService service) {
        this.materialService = service;
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

            materialService.importExcel(list);

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

        List<Material> list = materialService.exportAll();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode("物料数据", "UTF-8")
                .replaceAll("\\+", "%20");

        response.setHeader("Content-disposition",
                "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), Material.class)
                .sheet("物料数据")
                .doWrite(list);
    }

    /**
     * 物料下拉框
     */
    @GetMapping("/list")
    public Result<Object> list() {

        return Result.success(

                "查询成功",

                materialMapper.listMaterials()

        );
    }

    @GetMapping("/customer")
    public Result<Object> customer(

            @RequestParam
            String materialCode

    ) {

        return Result.success(

                "查询成功",

                materialMapper.getCustomerCode(
                        materialCode
                )

        );
    }


}
