package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单接口
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
