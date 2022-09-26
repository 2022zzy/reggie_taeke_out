package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单接口
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
