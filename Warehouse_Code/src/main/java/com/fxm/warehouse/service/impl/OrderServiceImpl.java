package com.fxm.warehouse.service.impl;

import com.fxm.warehouse.mapper.OrderMapper;
import com.fxm.warehouse.pojo.entity.Order;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.Result;
import com.fxm.warehouse.service.OrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 分页查询订单
     */
    @Override
    public PageBean<Order> page(Integer pageNum, Integer pageSize, String orderNumber, String customer, String customerPartNumber, String factoryPartNumber, String productName, String status, Date orderDate, Date deliverDate) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.page(orderNumber, customer, customerPartNumber, factoryPartNumber, productName, status, orderDate, deliverDate);
        PageInfo<Order> pageInfo = new PageInfo<>(orderList);
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public Order getById(Integer id) {
        return orderMapper.getById(id);
    }

    @Override
    public Result add(Order order) {
        // 插入订单
        boolean isAdded = orderMapper.add(order) > 0;
        if (isAdded) {
            return Result.success("订单添加成功！");
        } else {
            return Result.error("订单添加失败！");
        }
    }

    @Override
    public boolean update(Order order) {
        return orderMapper.update(order) > 0;
    }

    @Override
    public boolean delete(Integer id) {
        return orderMapper.delete(id) > 0;
    }

    @Override
    public boolean deleteBatch(Long[] ids) {
        return orderMapper.deleteBatch(ids) > 0;
    }

    @Override
    public void exportOrders(List<Long> orderIds, HttpServletResponse response) {
        // 根据 orderIds 查询符合条件的订单
        List<Order> orderList = orderMapper.findOrdersByIds(orderIds);

        // 创建 Excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("订单列表");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"订单号", "客户", "客户料号", "工厂料号", "产品名称", "订单日期", "交付日期", "订单数量",
                "已交付", "欠交数量", "投料进度", "备料进度", "外发进度", "欠料明细", "安装进度", "备注", "状态"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 用Java 8的DateTimeFormatter来格式化LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 填充数据
        int rowIdx = 1;
        for (Order order : orderList) {
            Row row = sheet.createRow(rowIdx++);
            int colIdx = 0;

            row.createCell(colIdx++).setCellValue(order.getOrderNumber());
            row.createCell(colIdx++).setCellValue(order.getCustomer());
            row.createCell(colIdx++).setCellValue(order.getCustomerPartNumber());
            row.createCell(colIdx++).setCellValue(order.getFactoryPartNumber());
            row.createCell(colIdx++).setCellValue(order.getProductName());

            // 订单日期
            Cell orderDateCell = row.createCell(colIdx++);
            if (order.getOrderDate() != null) {
                orderDateCell.setCellValue(order.getOrderDate().format(formatter));
            } else {
                orderDateCell.setCellValue("");
            }

            // 交付日期
            Cell deliveryDateCell = row.createCell(colIdx++);
            if (order.getDeliveryDate() != null) {
                deliveryDateCell.setCellValue(order.getDeliveryDate().format(formatter));
            } else {
                deliveryDateCell.setCellValue("");
            }

            row.createCell(colIdx++).setCellValue(order.getOrderQuantity());
            row.createCell(colIdx++).setCellValue(order.getDeliveredQuantity());
            row.createCell(colIdx++).setCellValue(order.getOrderQuantity() - order.getDeliveredQuantity());
            row.createCell(colIdx++).setCellValue(order.getMaterialProgress());
            row.createCell(colIdx++).setCellValue(order.getMaterialPreparationProgress());
            row.createCell(colIdx++).setCellValue(order.getOutsourcing());
            row.createCell(colIdx++).setCellValue(order.getMaterialshortagedetails() == null ? "暂无欠料明细" : order.getMaterialshortagedetails());
            row.createCell(colIdx++).setCellValue(order.getInstallationProgress());

            String remarks = order.getRemarks();
            if (remarks == null || remarks.trim().isEmpty()) {
                remarks = "暂无备注";
            }
            row.createCell(colIdx++).setCellValue(remarks);

            row.createCell(colIdx++).setCellValue(order.getStatus());
        }

        // 设置 HTTP 头部信息，告诉浏览器下载文件
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=orders.xlsx");

        try {
            // 写入 Excel 数据到 HTTP 响应流
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long countOrders() {
        return orderMapper.count();
    }


    @Override
    public List<String> getCustomer() {
        return orderMapper.getCustomer();
    }

    @Override
    public List<Map<String, Object>> countOrdersByRange(String range) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        switch (range.toLowerCase()) {
            case "week":
                startDate = endDate.minusDays(6); // 包含今天，总共7天
                break;
            case "month":
                startDate = endDate.minusMonths(1).plusDays(1); // 包含今天
                break;
            case "year":
                startDate = endDate.minusYears(1).plusDays(1);
                break;
            default:
                throw new IllegalArgumentException("不支持的范围类型: " + range);
        }

        return orderMapper.countOrdersBetween(startDate, endDate);
    }


}
