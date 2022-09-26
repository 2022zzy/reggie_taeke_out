package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.entity.User;

/**
 * 业务层接口
 */
public interface UserService extends IService<User> {

    /**
     * 邮箱验证的接口
     * @param to
     * @param subject
     * @param context
     */
//    public void sendMsg(String to,String subject,String context);

}
