package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

/**
 * ClassName: WorkSpaceService
 * Package: com.sky.service
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/4/7 11:18
 * @Version 1.0
 */
public interface WorkSpaceService {

    /**
     * 工作台数据概览统计
     *
     * @return
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 订单管理数据
     *
     * @return
     */
    OrderOverViewVO getOverviewOrders();

    /**
     * 菜品总览数据
     *
     * @return
     */
    DishOverViewVO getOverviewDishes();

    /**
     * 套餐总览
     *
     * @return
     */
    SetmealOverViewVO getOverviewSetmeals();
}
