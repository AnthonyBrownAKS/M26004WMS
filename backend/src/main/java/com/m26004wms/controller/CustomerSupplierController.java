package com.m26004wms.controller;

import com.alibaba.excel.EasyExcel;
import com.m26004wms.common.Result;
import com.m26004wms.dto.CustomerSupplierExcelDTO;
import com.m26004wms.entity.CustomerSupplier;
import com.m26004wms.service.CustomerSupplierService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/customerSupplier")
public class CustomerSupplierController {

    private final CustomerSupplierService service;

    public CustomerSupplierController(CustomerSupplierService service) {
        this.service = service;
    }

    /**
     * 导入xlsx表
     */
    @PostMapping("/import")
    public Result<String> importExcel(@RequestParam("file") MultipartFile file) {

        try {

            List<CustomerSupplierExcelDTO> list =
                    EasyExcel.read(file.getInputStream())
                            .head(CustomerSupplierExcelDTO.class)
                            .sheet()
                            .doReadSync();

            service.importExcel(list);

            return Result.success("导入成功");

        } catch (Exception e) {
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    /**
     * 导出xlsx表
     */
    @GetMapping("/exportAll")
    public void exportAll(HttpServletResponse response) throws Exception {

        // 查全量数据
        List<CustomerSupplier> list = service.exportAll();

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode("客商数据", "UTF-8")
                .replaceAll("\\+", "%20");

        response.setHeader("Content-disposition",
                "attachment;filename=" + fileName + ".xlsx");

        // 写出Excel（直接用Entity）
        EasyExcel.write(response.getOutputStream(), CustomerSupplier.class)
                .sheet("客商数据")
                .doWrite(list);
    }
}
