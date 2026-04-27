package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.m26004wms.dto.CustomerSupplierExcelDTO;
import com.m26004wms.entity.CustomerSupplier;
import com.m26004wms.mapper.CustomerSupplierMapper;
import com.m26004wms.service.CustomerSupplierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.LocalDateTime.parse;

@Service
public class CustomerSupplierServiceImpl
        extends ServiceImpl<CustomerSupplierMapper, CustomerSupplier>
        implements CustomerSupplierService {

    // 解决2025/5/20 07:15:36格式数据的转换
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");

    /**
     * 导出xlsx表
     */
    @Override
    public List<CustomerSupplier> exportAll() {
        return this.list(); // 查全表
    }

    /**
     * 导入xlsx表
     */
    @Transactional
    @Override
    public void importExcel(List<CustomerSupplierExcelDTO> list) {

        List<CustomerSupplier> entities = list.stream().map(dto -> {

            CustomerSupplier e = new CustomerSupplier();

            // =====================
            // 全字段映射
            // =====================
            e.setId(dto.getId());
            e.setWarehouseId(dto.getWarehouseId());
            e.setName(dto.getName());
            e.setCode(dto.getCode());
            e.setAddress(dto.getAddress());
            e.setEmail(dto.getEmail());
            e.setPhone(dto.getPhone());

            e.setIsCustomer(dto.getIsCustomer());
            e.setIsSupplier(dto.getIsSupplier());
            e.setIsOwner(dto.getIsOwner());

            e.setIsDeleted(dto.getIsDeleted());
            e.setDeleterId(dto.getDeleterId());
            e.setDeletionTime(parseTime(dto.getDeletionTime()));

            e.setLastModificationTime(parseTime(dto.getLastModificationTime()));
            e.setLastModifierId(dto.getLastModifierId());

            e.setCreationTime(parseTime(dto.getCreationTime()));
            e.setCreatorId(dto.getCreatorId());

            e.setExtraProperties(dto.getExtraProperties());
            e.setConcurrencyStamp(dto.getConcurrencyStamp());

            // =========================
            // ⭐ 核心：判断是否存在
            // =========================
            CustomerSupplier exist = this.lambdaQuery()
                    .eq(CustomerSupplier::getCode, dto.getCode())
                    .one();

            if (exist != null) {

                // 👉 更新
                e.setId(exist.getId());
                this.updateById(e);

            } else {

                // 👉 新增
                this.save(e);
            }
            return e;

        }).toList();
    }

    private LocalDateTime parseTime(String v) {
        if (v == null || v.trim().isEmpty()) {
            return null;
        }

        return LocalDateTime.parse(v, FORMATTER);
    }
}