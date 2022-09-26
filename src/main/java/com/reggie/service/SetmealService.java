package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.DishDto;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.Setmeal;

import java.util.List;

/**
 * 套餐接口
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐。同时需要删除套餐和菜品的关联信息
     * @param ids
     */
    public void removeWithDish(List<Long> ids);


    /**
     * 修改
     * @param id
     * @return
     */
    SetmealDto getSetmealData(Long id);
}
