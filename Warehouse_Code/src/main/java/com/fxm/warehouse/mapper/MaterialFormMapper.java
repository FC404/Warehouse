package com.fxm.warehouse.mapper;

import com.fxm.warehouse.pojo.entity.MaterialForm;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface MaterialFormMapper {
    List<MaterialForm> page(String customer, String parentPartNumber, String copperMaterialNumber, String childPartNumber, String materialSpecification, String materialType, Integer quantity, Integer copperQuantity, Integer orderQuantity, Integer plannedQuantity, String manufacturingType, String orderStatus, Date feedingDate, Date machiningDeliveryDate, Date orderDate, Date onlineDate);

    // 根据 ID 查询订单
    @Select("SELECT * FROM warehouse.material_form WHERE id = #{id}")
    MaterialForm getById(Integer id);

    @Insert("INSERT INTO warehouse.material_form (customer, parent_part_number, copper_material_number, child_part_number, material_specification, material_type, quantity, copper_quantity, order_quantity, planned_quantity, feeding_date, machining_delivery_date, manufacturing_type, order_date, online_date, order_status, remarks) " +
            "VALUES (#{customer}, #{parentPartNumber}, #{copperMaterialNumber}, #{childPartNumber}, #{materialSpecification}, #{materialType}, #{quantity}, #{copperQuantity}, #{orderQuantity}, #{plannedQuantity}, #{feedingDate}, #{machiningDeliveryDate}, #{manufacturingType}, #{orderDate}, #{onlineDate}, #{orderStatus}, #{remarks})")
    int add(MaterialForm materialForm);

    int update(MaterialForm materialForm);
    // 删除订单
    @Delete("DELETE FROM warehouse.material_form WHERE id = #{id}")
    int delete(Integer id);

    int deleteBatch(List<Integer> ids);

    List<MaterialForm> findMaterialFormByIds(List<Long> ids);

    @Select("SELECT COUNT(*) FROM warehouse.material_form")
    long countMaterialForm();

    @Select("select customer from `material_form` group by customer;")
    List<String> getCustomer();
}
