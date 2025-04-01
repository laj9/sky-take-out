package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: OrderTask
 * Package: com.sky.task
 * Description: 自动任务类
 *
 * @Author Aijing Liu
 * @Create 2025/4/1 19:15
 * @Version 1.0
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理支付超时订单
     */
    @Scheduled(cron = "0 * * * * ?")//每分钟检查
    public void processTimeoutOrder() {
        //select * from orders where status= and orderTime <
        log.info("开始处理支付超时订单：{}", LocalDateTime.now());

        //超过15分钟则为超时
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

        if (ordersList != null && ordersList.size() > 0) {
            for(Orders orders: ordersList){
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orders.setCancelReason("订单超时，无法支付");
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 处理正在配送中的订单
     */
//    @Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(cron = "1/5 * * * * ?")
    public void processDeliveryOrder(){
        log.info("开始处理正在配送中的订单：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if (ordersList != null && ordersList.size() > 0) {
            for(Orders orders: ordersList){
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
