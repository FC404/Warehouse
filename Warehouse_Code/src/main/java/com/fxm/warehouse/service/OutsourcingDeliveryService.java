package com.fxm.warehouse.service;

import com.fxm.warehouse.pojo.entity.OutsourcingDelivery;
import com.fxm.warehouse.pojo.entity.PageBean;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.List;

public interface OutsourcingDeliveryService {
    PageBean<OutsourcingDelivery> page(Integer pageNum, Integer pageSize, String supplier, String productCode, String isOnline, String productName, Date outsourceDate, Date plannedCompletionDate, Date returnDate);

    OutsourcingDelivery getById(Long id);

    boolean updateById(OutsourcingDelivery outsourcingDelivery);
    
    boolean removeById(Long id);

    boolean removeByIds(List<Long> list);

    void exportOutsourcingDelivery(List<Long> ids, HttpServletResponse response);

    long countOutsourcingDelivery();

    List<String> getSupplierNames();


    void save(OutsourcingDelivery outsourcingDelivery);
}
