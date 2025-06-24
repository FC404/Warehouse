package com.fxm.warehouse.mapper;

import com.fxm.warehouse.pojo.entity.Order;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    // 分页查询订单
    List<Order> page(String orderNumber, String customer, String customerPartNumber, String factoryPartNumber, String productName, String status, Date orderDate, Date deliveryDate);

    // 根据 ID 查询订单
    @Select("SELECT * FROM warehouse.`order` WHERE id = #{id}")
    Order getById(Integer id);

    // 新增订单
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int add(Order order);


    // 更新订单
    @Update("UPDATE warehouse.`order` SET customer=#{customer}, orderNumber=#{orderNumber}, orderDate=#{orderDate}, deliveryDate=#{deliveryDate}, " +
            "customerPartNumber=#{customerPartNumber}, factoryPartNumber=#{factoryPartNumber}, orderQuantity=#{orderQuantity}, " +
            "deliveredQuantity=#{deliveredQuantity}, status=#{status}, remarks=#{remarks}, productName=#{productName}, " +
            "materialProgress=#{materialProgress}, materialPreparationProgress=#{materialPreparationProgress}, installationProgress=#{installationProgress} ,materialshortagedetails=#{materialshortagedetails} , outsourcing =#{outsourcing}" +
            "WHERE id = #{id}")
    int update(Order order);

    @Select("SELECT COUNT(*) FROM warehouse.`order`")
    long count();
    // 删除订单
    @Delete("DELETE FROM warehouse.`order` WHERE id = #{id}")
    int delete(Integer id);

    // 批量删除订单
    int deleteBatch(Long[] ids);

    Order findByOrderNumber(String orderNumber);

    List<Order> findOrdersByIds(List<Long> orderIds);


    List<String> getCustomer();


    List<Map<String, Object>> countOrdersBetween(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

}
