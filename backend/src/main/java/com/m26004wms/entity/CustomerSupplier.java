package com.m26004wms.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.m26004wms.config.BooleanToStringConverter;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("customer_supplier")
public class CustomerSupplier {

    /** 主键ID */
    @TableId("id")
    private String id;

    /** 所属仓库ID */
    private String warehouseId;

    /** 名称 */
    private String name;

    /** 编码 */
    private String code;

    /** 地址 */
    private String address;

    /** 邮箱 */
    private String email;

    /** 电话 */
    private String phone;

    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isCustomer;

    /** 是否供应商 */
    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isSupplier;

    /** 是否所有者 */
    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isOwner;

    /** 是否删除（逻辑删除） */
    @ExcelProperty(converter = BooleanToStringConverter.class)
    private Boolean isDeleted;

    /** 删除人ID */
    private String deleterId;

    /** 删除时间 */
    private LocalDateTime deletionTime;

    /** 最后修改时间 */
    private LocalDateTime lastModificationTime;

    /** 最后修改人ID */
    private String lastModifierId;

    /** 创建时间 */
    private LocalDateTime creationTime;

    /** 创建人ID */
    private String creatorId;

    /** 扩展属性（JSON存储） */
    private String extraProperties;

    /** 并发控制（乐观锁） */
    private String concurrencyStamp;
}
