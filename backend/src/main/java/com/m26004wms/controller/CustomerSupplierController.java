package com.m26004wms.controller;

import com.alibaba.excel.EasyExcel;
import com.m26004wms.common.LogUtil;
import com.m26004wms.common.Result;
import com.m26004wms.dto.CustomerSupplierExcelDTO;
import com.m26004wms.entity.CustomerSupplier;
import com.m26004wms.entity.Logs;
import com.m26004wms.entity.Material;
import com.m26004wms.mapper.CustomerSupplierMapper;
import com.m26004wms.mapper.LogMapper;
import com.m26004wms.service.CustomerSupplierService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/customerSupplier")
public class CustomerSupplierController {

    @Autowired
    private CustomerSupplierService service;

    @Autowired
    private CustomerSupplierMapper mapper;

    @Autowired
    private LogMapper logMapper;

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

            // 操作日志
            Logs log = new Logs();
            log.setType("INSERT");
            log.setResult("SUCCESS");
            log.setParam("IMPORT CUSTOMER EXCEL INTO DATABASE");
            logMapper.insertControl(log);

            return Result.success("导入成功");

        } catch (Exception e) {

            // 操作日志
            Logs log = new Logs();
            log.setType("INSERT");
            log.setResult("FAIL");
            log.setParam("IMPORT CUSTOMER EXCEL INTO DATABASE");
            logMapper.insertControl(log);

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

        // 操作日志
        Logs log = new Logs();
        log.setType("SELECT");
        log.setResult("SUCCESS");
        log.setParam("EXPORT CUSTOMER EXCEL FROM DATABASE");
        logMapper.insertControl(log);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<Object> page(

            @RequestParam Integer current,

            @RequestParam Integer size,

            @RequestParam(required = false) String code
    ) {

        // 日志
        if (!code.isEmpty())
            LogUtil.success(
                    logMapper,
                    "SELECT",
                    "SELECT CUSTOMER BY CODE( " + code + " )"
            );

        return Result.success(service.page(current, size, code));

    }

    /**
     * 根据code查询
     */
    @PostMapping("/search/{code}")
    public Result<CustomerSupplier> search(
            @PathVariable String code
    ) {

        CustomerSupplier customerSupplier = mapper.getByCode(code);

        // 操作日志
        Logs log = new Logs();
        log.setType("SELECT");
        log.setResult("CUSTOMER: " + customerSupplier.toString());
        log.setParam("SEARCH CUSTOMER BY CODE( " + code + " )");
        logMapper.insertControl(log);

        return Result.success(customerSupplier);

    }

    /**
     * 新增/修改
     */
    @PostMapping("/add")
    public Result<String> add(
            @RequestBody CustomerSupplier customerSupplier
    ) {

        // 填补空白数据
        customerSupplier.setLastModificationTime(LocalDateTime.now());
        if (customerSupplier.getId() == null) customerSupplier.setCreationTime(LocalDateTime.now());
        customerSupplier.setAddress("中国");
        customerSupplier.setWarehouseId("3a17720d-9b95-f2b2-698a-4bf204c10f8f");

        mapper.insertOrUpdate(customerSupplier);

        // 操作日志
        Logs log = new Logs();
        log.setType("UPDATE");
        log.setResult("SUCCESS");
        log.setParam("UPDATE CUSTOMER( NAME:"+ customerSupplier.getName() + " ) INTO DATABASE");
        logMapper.insertControl(log);

        return Result.success();

    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable String id) {

        // 操作日志
        CustomerSupplier cs = mapper.selectById(id);
        Logs log = new Logs();
        log.setType("DELETE");
        log.setResult("SUCCESS");
        log.setParam("DELETE CUSTOMER( NAME:"+ cs.getName() + " ) FROM DATABASE");
        logMapper.insertControl(log);

        mapper.deleteById(id);
        return Result.success();

    }
}
