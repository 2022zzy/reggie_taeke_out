package com.reggie.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
* 通用返回结果，服务端响应的数据最终都回封装成此对象
* */
@Data
@ApiModel("套餐")
public class R<T> implements Serializable {

    @ApiModelProperty("编码")
    private Integer code; //编码：1成功，0和其它数字为失败

    @ApiModelProperty("错误信息")
    private String msg; //错误信息

    @ApiModelProperty("数据")
    private T data; //数据

    @ApiModelProperty("动态数据")
    private Map map = new HashMap(); //动态数据


    public static <T> R<T> success(T object) {
        R<T> res = new R<T>();
        res.data = object;
        res.code = 1;
        return res;
    }


    public static <T> R<T> error(String msg) {
        R res = new R();
        res.msg = msg;
        res.code = 0;
        return res;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }



}

