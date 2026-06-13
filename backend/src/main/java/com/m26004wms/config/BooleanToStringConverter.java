package com.m26004wms.config;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.enums.CellDataTypeEnum;

public class BooleanToStringConverter implements Converter<Boolean> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Boolean.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public WriteCellData<?> convertToExcelData(Boolean value,
                                               ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {

        if (value == null) {
            return new WriteCellData<>("");
        }

        return new WriteCellData<>(value ? "True" : "False");
    }
}
