package com.m26004wms.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.m26004wms.common.LogUtil;
import com.m26004wms.common.Result;
import com.m26004wms.dto.MaterialExcelDTO;
import com.m26004wms.entity.Material;
import com.m26004wms.entity.MaterialContainer;
import com.m26004wms.entity.MaterialRequest;
import com.m26004wms.entity.Outbound;
import com.m26004wms.mapper.LogMapper;
import com.m26004wms.mapper.MaterialContainerMapper;
import com.m26004wms.mapper.MaterialMapper;
import com.m26004wms.service.MaterialService;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private MaterialContainerMapper materialContainerMapper;

    @Autowired
    private LogMapper logMapper;


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

            // 日志记录
            LogUtil.success(
                    logMapper,
                    "INSERT",
                    "导入物料表IMPORT MATERIAL EXCEL INTO DATABASE"
            );

            return Result.success("导入成功");

        } catch (Exception e) {

            LogUtil.fail(
                    logMapper,
                    "INSERT",
                    "导入失败",
                    "导入物料表IMPORT MATERIAL EXCEL INTO DATABASE"
            );

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

        // 日志记录
        LogUtil.success(
                logMapper,
                "SELECT",
                "导出物料表EXPORT MATERIAL EXCEL INTO DATABASE"
        );
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
                    "搜索物料SELECT MATERIAL BY CODE( " + code + " )"
            );

        return Result.success(materialService.page(current, size, code));

    }

    /**
     * 查询全部已绑定容器的物料
     */
    @GetMapping("/listC")
    public Result<List<MaterialRequest>> listC() {

        List<MaterialContainer> list = materialContainerMapper.selectList(null);
        List<MaterialRequest> materials = new LinkedList<>();
        Material material;

        for(MaterialContainer mc : list){
            material = materialMapper.getByCustomerCode(mc.getMaterialCode());

            MaterialRequest materialRequest = getMaterialRequest(mc, material);

            materials.add(materialRequest);
        }

        return Result.success(materials);
    }

    private static @NonNull MaterialRequest getMaterialRequest(MaterialContainer mc, Material material) {
        MaterialRequest materialRequest = new MaterialRequest();

        materialRequest.setId(material.getId());
        materialRequest.setName(material.getName());
        materialRequest.setSpec(material.getSpec());
        materialRequest.setCode(material.getCode());
        materialRequest.setContainerId(mc.getContainerId());
        materialRequest.setBatch(mc.getBatch());
        materialRequest.setQuantity(mc.getQuantity());
        materialRequest.setCustomerCode(mc.getCustomerCode());
        return materialRequest;
    }

    /**
     * 查询全部物料
     */
    @GetMapping("/list")
    public Result<List<Material>> list() {
        return Result.success(materialService.list());
    }

    /**
     * 根据code查询
     */
    @PostMapping("/search/{code}")
    public Result<Material> search(
            @PathVariable String code
    ) {

        Material material = materialMapper.getByCustomerCode(code);

        return Result.success(material);

    }

    /**
     * 新增/修改
     */
    @PostMapping("/add")
    public Result<String> add(
            @RequestBody Material material
    ) {
        // 日志
        String type = materialMapper.selectById(material.getId()) == null ? "INSERT" : "UPDATE";
        LogUtil.success(
                logMapper,
                type,
                "修改物料EDIT :" + material
        );

        material.setShortCode(material.getCode());
        material.setCreationTime(LocalDateTime.now());

        materialMapper.insertOrUpdate(material);

        return Result.success();

    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable String id) {

        LogUtil.success(
                logMapper,
                "DELETE",
                "删除物料DELETE :" + materialMapper.selectById(id)
        );

        materialMapper.deleteById(id);
        return Result.success();

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

    @GetMapping("/inbound")
    public Result<List<MaterialContainer>> getMaterialContainer(){
        return Result.success(materialContainerMapper.selectList(null));
    }



}
