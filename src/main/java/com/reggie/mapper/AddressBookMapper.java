package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地址接口
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
