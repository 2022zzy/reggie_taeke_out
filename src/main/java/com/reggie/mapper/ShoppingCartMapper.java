package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物mapper接口
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
