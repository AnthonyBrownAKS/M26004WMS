package com.m26004wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m26004wms.entity.CustomerSupplier;
import com.m26004wms.entity.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerSupplierMapper extends BaseMapper<CustomerSupplier> {

    /**
     * 通过code查询
     */
    @Select("""

        select *
 
        from customer_supplier

        where code = #{code}
        
        limit 1
        
        """)
    CustomerSupplier getByCode(
            @Param("code")
            String code
    );


    /**
     * 分页
     */
    @Select("""

            select *

            from customer_supplier

            where

            (
                #{code} is null
                or
                #{code} = ''
                or
                code like concat('%', #{code}, '%')
            )

            order by creation_time desc

            limit #{offset}, #{size}

            """)
    List<CustomerSupplier> selectPage(
            @Param("offset")
            int offset,

            @Param("size")
            int size,

            @Param("code")
            String code
    );


    @Select("""

            select count(*)

            from customer_supplier

            where

            (
                #{code} is null
                or
                #{code} = ''
                or
                code like concat('%', #{code}, '%')
            )

            """)
    Long selectCount(
            @Param("code")
            String code
    );

}
