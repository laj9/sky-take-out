package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: WorkSpaceServiceImpl
 * Package: com.sky.service.impl
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/4/7 11:19
 * @Version 1.0
 */
@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 工作台数据概览统计
     *
     * @return
     */
    public BusinessDataVO getBusinessData() {
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime beginTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

        //营业额：select sum(amount) from orders where order_time > ? and order_time < ? and status = 5
        Map map1 = new HashMap();
        map1.put("begin", beginTime);
        map1.put("end", endTime);
        map1.put("status", Orders.COMPLETED);
        Double turnover = orderMapper.sumByMap(map1);
        turnover = turnover == null ? 0.0 : turnover;

        //有效订单：select count(id) from orders where order_time > ? and order_time < ? and status = 5
        Integer validOrderCount = orderMapper.getOrdersNumber(map1);

        //总订单数: select count(id) from orders where order_time > ? and order_time < ?
        Map map2 = new HashMap();
        map2.put("begin", beginTime);
        map2.put("end", endTime);
        Integer totalOrderCount = orderMapper.getOrdersNumber(map2);

        //订单完成率：有效订单/总订单
        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;

        //平均客单价：营业额/有效订单
        Double unitPrice = turnover.doubleValue() / validOrderCount;

        //新增用户：select count(id) from user where create_time > ? and create_time < ?
        Integer newUsers = userMapper.getNewUser(beginTime, endTime);

        return BusinessDataVO
                .builder()
                .validOrderCount(validOrderCount)
                .newUsers(newUsers)
                .orderCompletionRate(orderCompletionRate)
                .turnover(turnover)
                .unitPrice(unitPrice)
                .build();
    }

    /**
     * 订单管理数据
     *
     * @return
     */
    public OrderOverViewVO getOverviewOrders() {
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime beginTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

        Map map1 = new HashMap();
        map1.put("begin", beginTime);
        map1.put("end", endTime);
        Integer allOrders = orderMapper.getOrdersNumber(map1);

        map1.put("status", Orders.CANCELLED);
        Integer cancellerOrders = orderMapper.getOrdersNumber(map1);

        map1.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.getOrdersNumber(map1);

        map1.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.getOrdersNumber(map1);

        map1.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.getOrdersNumber(map1);
        return OrderOverViewVO
                .builder()
                .allOrders(allOrders)
                .cancelledOrders(cancellerOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .build();
    }

    /**
     * 菜品总览数据
     *
     * @return
     */
    public DishOverViewVO getOverviewDishes() {
        Integer sold = dishMapper.getByStatus(1);
        Integer discontinued = dishMapper.getByStatus(1);
        return DishOverViewVO
                .builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    /**
     * 套餐总览
     *
     * @return
     */
    public SetmealOverViewVO getOverviewSetmeals() {
        Integer sold = setmealMapper.getByStatus(1);
        Integer discontinued = setmealMapper.getByStatus(0);
        return SetmealOverViewVO
                .builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }
}
