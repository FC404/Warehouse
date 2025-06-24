package com.fxm.warehouse.service;

import com.fxm.warehouse.pojo.entity.Order;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.Result;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderService {

    /** 分页查询订单 */

    PageBean<Order> page(Integer pageNum, Integer pageSize, String orderNumber, String customer, String customerPartNumber, String factoryPartNumber, String productName, String status, Date orderDate, Date deliverDate);

    /** 根据 ID 查询订单 */
    Order getById(Integer id);

    /**
     * 新增订单
     */
    Result add(Order order);

    /** 更新订单 */
    boolean update(Order order);

    /** 删除订单 */
    boolean delete(Integer id);

    /** 批量删除订单 */
    boolean deleteBatch(Long[] ids);


    void exportOrders(List<Long> orderIds, HttpServletResponse response);

    long countOrders();



    List<String> getCustomer();


    List<Map<String, Object>> countOrdersByRange(String range);


}
