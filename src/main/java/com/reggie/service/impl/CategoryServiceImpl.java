package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//业务层实现类

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param ids
     */

    @Override
    public void remove(Long ids) {
        //        select count(*) from dish where category_id=?
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类的id 进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        //  注意: count统计查询不要忘记 加上过滤条件
        int count1 = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联菜品，如果已关联。抛出一个业务异常
        if (count1 > 0){
            //已经关联菜品。抛出一个业务异常 ,当前分类 关联到其他菜品(Dish),抛出异常，且交给 全局异常处理类GlobalExceptionHander来处理
            throw new CustomException("当前分类下关联了菜品,不能删除");

        }

        //查询当前分类是否关联套餐，如果已关联。抛出一个业务异常
        //  select count(*) from setmeal where category_id=?
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类的id 进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0){
            //已经关联套餐。抛出一个业务异常
            throw new CustomException("当前分类已经关联到有关套餐，不能删除当前分类！");
        }

        //正常删除分类.   当前分类 没有关联菜品或套餐，可以正常地删除分类(调用MP 提供的remove方法)
        super.removeById(ids);
    }

}
