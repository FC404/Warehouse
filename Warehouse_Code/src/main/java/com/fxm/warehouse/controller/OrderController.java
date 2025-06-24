package com.fxm.warehouse.controller;

import com.fxm.warehouse.anno.Log;
import com.fxm.warehouse.pojo.entity.Order;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.Result;
import com.fxm.warehouse.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询订单
     */
    @GetMapping("/list")
    public Result<PageBean<Order>> getAllOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNumber, // 订单号搜索
            @RequestParam(required = false) String customer, // 客户搜索
            @RequestParam(required = false) String customerPartNumber, // 状态筛选
            @RequestParam(required = false) String factoryPartNumber,// 状态筛选
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String status, // 状态筛选
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderDate, // 订单日期
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date deliveryDate // 交货日期
    ) {
        // 调用 service 进行查询
        PageBean<Order> pageBean = orderService.page(pageNum, pageSize, orderNumber,customer,customerPartNumber,factoryPartNumber, productName,status,  orderDate, deliveryDate);
        return Result.success(pageBean);
    }


    /**
     * 根据ID查询订单
     */
    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        return order != null ? Result.success(order) : Result.error("订单不存在");
    }

    /**
     * 新增订单
     */
    @PostMapping("/add")
    @Log
    public Result addOrder(@RequestBody Order order) {
        return orderService.add(order);
    }

    /**
     * 更新订单
     */
    @PutMapping("/{id}")
    @Log
    public Result<Void> updateOrder(@PathVariable Integer id, @RequestBody Order order) {
        order.setId(id); // 确保 ID 一致
        boolean success = orderService.update(order);
        return success ? Result.success() : Result.error("更新订单失败");
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    @Log
    public Result<Void> deleteOrder(@PathVariable Integer id) {
        boolean success = orderService.delete(id);
        return success ? Result.success() : Result.error("删除订单失败");
    }

    /**
     * 批量删除订单
     */
    @DeleteMapping("/deleteBatch")
    @Log
    public Result<Void> deleteOrders(@RequestBody Long[] ids) {
        boolean success = orderService.deleteBatch(ids);
        return success ? Result.success() : Result.error("批量删除订单失败");
    }



    @PostMapping("/export")
    @Log
    public void exportOrders(@RequestBody List<Long> orderIds, HttpServletResponse response) {
        orderService.exportOrders(orderIds, response);
    }

    // 统计订单总数
    @GetMapping("/count")
    public Result<Long> countOrders() {
        long count = orderService.countOrders();
        return Result.success(count);
    }

    @GetMapping("/countOrdersByRange")
    public Result<List<Map<String, Object>>> countOrdersByRange(@RequestParam String range) {
        List<Map<String, Object>> result = orderService.countOrdersByRange(range);
        return Result.success(result);
    }


    //获取订单客户
    @GetMapping("/getCustomer")
    public Result<List<String>> getCustomer() {
        List<String> customer = orderService.getCustomer();
        return Result.success(customer);
    }

}
