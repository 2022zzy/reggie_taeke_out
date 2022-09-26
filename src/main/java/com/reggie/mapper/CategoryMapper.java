package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.Category;
import com.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
