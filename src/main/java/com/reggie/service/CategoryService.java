package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.entity.Category;
import com.reggie.entity.Employee;

/**
 * 业务层接口
 */
public interface CategoryService extends IService<Category> {

    public void remove(Long ids);
}
