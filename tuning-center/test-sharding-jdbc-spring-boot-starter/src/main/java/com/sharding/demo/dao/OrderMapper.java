package com.sharding.demo.dao;

import com.sharding.demo.model.Order;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface OrderMapper {
    int save(Order info);

    /**
     * 批量保存
     * @param list
     * @return
     */
    int batchSave(List<Order> list);

}
