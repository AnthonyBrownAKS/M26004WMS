package com.m26004wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m26004wms.entity.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface MaterialMapper extends BaseMapper<Material>{

    /**
     * 查询物料列表
     */
    @Select("""

            select
                code,
                name,
                spec

            from material

            order by code

            """)
    List<Map<String, Object>> listMaterials();

    @Select("""

        select customer_code

        from material_container

        where material_code = #{materialCode}

        limit 1

        """)
    String getCustomerCode(

            @Param("materialCode")
            String materialCode

    );

    /**
     * 通过code查询 spec, name
     */
    @Select("""

        select *
 
        from material

        where code = #{materialCode}
        
        limit 1
        
        """)
    Material getByCustomerCode(
            @Param("materialCode")
            String materialCode
    );

}
