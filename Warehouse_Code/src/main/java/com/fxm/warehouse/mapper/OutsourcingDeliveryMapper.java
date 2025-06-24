package com.fxm.warehouse.mapper;

import com.fxm.warehouse.pojo.entity.OutsourcingDelivery;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface OutsourcingDeliveryMapper {

    List<OutsourcingDelivery> page(String supplier, String productCode, String isOnline,String productName, Date outsourceDate,Date plannedCompletionDate,Date returnDate);
    // 根据 ID 查询
    @Select("SELECT * FROM warehouse.outsourcing_delivery WHERE id = #{id}")
    OutsourcingDelivery selectById(Long id);


    int updateById(OutsourcingDelivery outsourcingDelivery);

    // 插入新记录
    void insert(OutsourcingDelivery outsourcingDelivery);

    // 删除记录
    @Delete("DELETE FROM warehouse.outsourcing_delivery WHERE id = #{id}")
    int deleteById(Long id);

    int deleteBatchIds(List<Long> list);

    List<OutsourcingDelivery> findOutsourcingDeliveryByIds(@Param("Ids")List<Long> ids);

    @Select("SELECT COUNT(*) FROM warehouse.outsourcing_delivery")
    long countOutsourcingDelivery();

    @Select("SELECT DISTINCT supplier FROM warehouse.outsourcing_delivery")
    List<String> getSupplierNames();
}
