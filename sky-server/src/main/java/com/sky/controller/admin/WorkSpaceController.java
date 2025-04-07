package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: WorkSpaceController
 * Package: com.sky.controller.admin
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/4/7 11:15
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/workspace")
@Api(tags = "工作台相关接口")
@Slf4j
public class WorkSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 工作台数据概览统计
     *
     * @return
     */
    @GetMapping("/businessData")
    @ApiOperation("数据概览")
    public Result<BusinessDataVO> getBusinessData() {
        return Result.success(workSpaceService.getBusinessData());
    }

    /**
     * 订单管理数据
     * @return
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("订单管理数据")
    public Result<OrderOverViewVO> getOverviewOrders(){
        return Result.success(workSpaceService.getOverviewOrders());
    }

    /**
     * 菜品总览数据
     * @return
     */
    @GetMapping("/overviewDishes")
    @ApiOperation("菜品总览")
    public Result<DishOverViewVO> getOverviewDishes(){
        return Result.success(workSpaceService.getOverviewDishes());
    }

    /**
     * 套餐总览
     * @return
     */
    @GetMapping("/overviewSetmeals")
    @ApiOperation("套餐总览")
    public Result<SetmealOverViewVO> getOverviewSetmeals(){
        return Result.success(workSpaceService.getOverviewSetmeals());
    }
}
