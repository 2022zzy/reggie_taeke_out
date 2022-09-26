package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 接口
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
