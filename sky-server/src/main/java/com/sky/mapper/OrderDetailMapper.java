package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: OrderDatailMapper
 * Package: com.sky.mapper
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/3/29 21:23
 * @Version 1.0
 */
@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单数据
     * @param orderDetails
     */
    void insertBatch(List<OrderDetail> orderDetails);
}
