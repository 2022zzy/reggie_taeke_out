package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.dto.DishDto;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.DishFlavor;
import com.reggie.entity.Setmeal;
import com.reggie.entity.SetmealDish;
import com.reggie.mapper.SetmeaMapper;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实现类
 */

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmeaMapper, Setmeal> implements SetmealService {

    // 自动装配
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息。操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
           item.setSetmealId(setmealDto.getId());
           return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息。操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);


    }

    /**
     * 删除套餐。同时需要删除套餐和菜品的关联信息
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //select count(*) from setmeal where id in(1,2,3) and status =1
        //查询套餐状态，确定是否可用删除
        final LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        final int count = this.count(queryWrapper);
        if (count > 0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐真正售卖中，不能删除");
        }

        //如果可用删除，先删除套餐表中的数据---setmeal
        this.removeByIds(ids);

        //批量删除
        //delete from setmeal dish setmel id in(1,2,3)
        LambdaQueryWrapper<SetmealDish>lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //删除关系表中的数据---setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);
        
    }


    //修改
    @Override
    public SetmealDto getSetmealData(Long id) {
        //构造修改构造器对象
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        //条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(id != null,SetmealDish::getSetmealId,id);

        if (setmeal != null){
            BeanUtils.copyProperties(setmeal,setmealDto);

            //添加当前提交过来的口味数据
            List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(dishes);

            return setmealDto;
        }

        return null;
    }

}
