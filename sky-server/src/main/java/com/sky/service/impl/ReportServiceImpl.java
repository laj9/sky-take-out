package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ReportServiceImpl
 * Package: com.sky.service.impl
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/4/6 14:56
 * @Version 1.0
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //创建日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnoverList = new ArrayList<>();
        //创建营业额表
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //select sum(amount) from orders where order_time < endTime and order_time > beginTime and status =
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        //创建返回实例
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户数据统计
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //返回横坐标：日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //新增用户数列表
        List<Integer> newUserList = new ArrayList<>();
        //select count(id) from user where createTime < ? and createTime > ?
        for (LocalDate date : dateList) {
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            Integer number = userMapper.getNewUser(beginTime, endTime);
            newUserList.add(number);
        }

        //总用户量列表
        List<Integer> totalUserList = new ArrayList<>();
        //select count(id) from user where createTime < ?
        for (LocalDate date : dateList) {
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer number = userMapper.getTotalUser(endTime);
            totalUserList.add(number);
        }

        //创建返回实例
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 订单数据统计
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //返回横坐标：日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //订单数列表
        List<Integer> orderCountList = new ArrayList<>();
        //有效订单数列表
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //订单数列表添加
            Map map1 = new HashMap();
            map1.put("begin", beginTime);
            map1.put("end", endTime);
            Integer orderCount = orderMapper.getOrdersNumber(map1);
            orderCountList.add(orderCount);
            //有效订单列表添加
            Map map2 = new HashMap();
            map2.put("begin", beginTime);
            map2.put("end", endTime);
            map2.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.getOrdersNumber(map2);
            validOrderCountList.add(validOrderCount);
        }

        //订单总数
        Integer totalOrderCount = 0;
        for (Integer i : orderCountList) {
            totalOrderCount += i;
        }

        //有效订单数
        Integer validOrderCount = 0;
        for (Integer i : validOrderCountList) {
            validOrderCount += i;
        }

        //订单完成率
        //不能写成Double orderCompletionRate = validOrderCount / totalOrderCount + 0.0;
        //因为相当于计算了两个int类型数据相除后（只可能是0.0或1.0）再转成double
        //所以先将其中一个直接转换成double再除
        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount ;
        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCount(validOrderCount)
                .totalOrderCount(totalOrderCount)
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .build();
    }
}
