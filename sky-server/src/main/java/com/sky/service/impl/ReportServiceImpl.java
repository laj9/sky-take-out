package com.sky.service.impl;

import com.sky.service.ReportService;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private WorkSpaceService workSpaceService;

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
        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
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

    /**
     * 菜品/套餐top10统计
     *
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
        List<String> names = salesTop10.stream()       // 1. 将 List 转换成 Stream
                .map(GoodsSalesDTO::getName)              // 2. 提取每个对象的 name 字段
                //GoodsSalesDTO::getName 是 方法引用，相当于 dto -> dto.getName()，即从每个 GoodsSalesDTO 对象中提取 name 字段。
                .collect(Collectors.toList());             // 3. 收集成新的 List<String>
        String nameList = StringUtils.join(names, ",");
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        return SalesTop10ReportVO
                .builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 导出运营数据报表
     *
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) {
        //查询数据库，获取营业数据
        LocalDate beginTime = LocalDate.now().minusDays(30);
        LocalDate endTime = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workSpaceService.getBusinessData(LocalDateTime.of(beginTime, LocalTime.MIN), LocalDateTime.of(endTime, LocalTime.MAX));

        Double orderCompletionRate = businessData.getOrderCompletionRate();
        orderCompletionRate = orderCompletionRate == null ? 0.00 : orderCompletionRate;

        Double unitPrice = businessData.getUnitPrice();
        unitPrice = unitPrice == null ? 0.00 : unitPrice;

        //通过POI将数据写入文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //基于模板创建新的excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            XSSFSheet sheet = excel.getSheet("sheet1");

            sheet.getRow(1).getCell(1).setCellValue("时间为：" + beginTime + "至" + endTime);

            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(orderCompletionRate);
            row.getCell(6).setCellValue(businessData.getNewUsers());

            XSSFRow row1 = sheet.getRow(4);
            row1.getCell(2).setCellValue(businessData.getValidOrderCount());
            row1.getCell(4).setCellValue(unitPrice);

            //明细数据填充
            for (int i = 0; i < 30; i++) {
                beginTime = beginTime.plusDays(1);
                LocalDateTime begin = LocalDateTime.of(beginTime, LocalTime.MIN);
                LocalDateTime end = LocalDateTime.of(beginTime, LocalTime.MAX);

                BusinessDataVO businessData1 = workSpaceService.getBusinessData(begin, end);

                Double unitPrice1 = businessData1.getUnitPrice();
                unitPrice1 = unitPrice1 == null ? 0.00 : unitPrice;
                Double orderCompletionRate1 = businessData1.getOrderCompletionRate();
                orderCompletionRate1 = orderCompletionRate == null ? 0.00 : orderCompletionRate;

                XSSFRow row2 = sheet.getRow(7 + i);
                row2.getCell(1).setCellValue(beginTime.toString());
                row2.getCell(2).setCellValue(businessData1.getTurnover());
                row2.getCell(3).setCellValue(businessData1.getValidOrderCount());
                row2.getCell(4).setCellValue(orderCompletionRate1);
                row2.getCell(5).setCellValue(unitPrice1);
                row2.getCell(6).setCellValue(businessData1.getNewUsers());
            }
            //通过输出流将excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
