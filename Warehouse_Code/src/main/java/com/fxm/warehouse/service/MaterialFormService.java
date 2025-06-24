package com.fxm.warehouse.service;

import com.fxm.warehouse.pojo.entity.MaterialForm;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.Result;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.List;

public interface MaterialFormService {
    PageBean<MaterialForm> page(Integer pageNum, Integer pageSize, String customer, String parentPartNumber, String copperMaterialNumber, String childPartNumber, String materialSpecification, String materialType, Integer quantity, Integer copperQuantity, Integer orderQuantity, Integer plannedQuantity, String manufacturingType, String orderStatus, Date feedingDate, Date machiningDeliveryDate, Date orderDate, Date onlineDate);

    MaterialForm getById(Integer id);

    Result add(MaterialForm materialForm);

    boolean update(MaterialForm materialForm);

    boolean delete(Integer id);

    boolean  deleteBatch(List<Integer> ids);

    void exportmaterialForm(List<Long> ids, HttpServletResponse response);

    long countMaterialForm();

    List<String> getCustomer();
}
