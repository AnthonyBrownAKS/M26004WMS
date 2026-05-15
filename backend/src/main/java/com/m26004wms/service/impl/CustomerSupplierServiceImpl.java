package com.m26004wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.m26004wms.dto.CustomerSupplierExcelDTO;
import com.m26004wms.entity.CustomerSupplier;
import com.m26004wms.entity.Material;
import com.m26004wms.mapper.CustomerSupplierMapper;
import com.m26004wms.service.CustomerSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerSupplierServiceImpl
        extends ServiceImpl<CustomerSupplierMapper, CustomerSupplier>
        implements CustomerSupplierService {

    @Autowired
    private CustomerSupplierMapper customerSupplierMapper;

    // 解决时间问题
    private static final List<DateTimeFormatter> FORMATTERS = List.of(

            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),

            DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss"),

            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),

            DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")

    );

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
            // 判断是否存在
            // =========================
            CustomerSupplier exist = this.lambdaQuery()
                    .eq(CustomerSupplier::getCode, dto.getCode())
                    .one();

            if (exist != null) {

                // 更新
                e.setId(exist.getId());
                this.updateById(e);

            } else {

                // 新增
                this.save(e);
            }
            return e;

        }).toList();
    }

    public static LocalDateTime parseTime(String value) {

        if (value == null || value.trim().isEmpty()) {

            return null;

        }

        value = value.trim();

        for (DateTimeFormatter formatter : FORMATTERS) {

            try {

                return LocalDateTime.parse(value, formatter);

            } catch (Exception ignored) {

            }

        }

        throw new RuntimeException(
                "无法解析时间格式: " + value
        );

    }

    @Override
    public Object page(Integer current, Integer size, String code) {

        int offset = (current - 1) * size;
        List<CustomerSupplier> records = customerSupplierMapper.selectPage(offset, size, code);

        Long total = customerSupplierMapper.selectCount(code);

        long pages = (total + size - 1) / size;

        Map<String, Object> result =
                new HashMap<>();

        result.put("records", records);

        result.put("current", current);

        result.put("pages", pages);

        result.put("size", size);

        result.put("total", total);

        return result;

    }
}