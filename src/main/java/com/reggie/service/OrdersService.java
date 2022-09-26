package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.entity.OrderDetail;
import com.reggie.entity.Orders;

import java.util.List;


/**
 * 订单接口
 */
public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);

    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId);


}
