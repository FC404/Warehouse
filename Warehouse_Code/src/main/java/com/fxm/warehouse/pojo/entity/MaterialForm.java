package com.fxm.warehouse.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialForm {

    private Long id;  // 主键，自动递增

    private String customer;  // 客户

    private String parentPartNumber;  // 母件料号


    private String copperMaterialNumber;  // 铜材料号


    private String childPartNumber;  // 子件料号


    private String materialSpecification;  // 材料规格


    private String materialType;  // 材质 (Enum 类型)


    private int quantity;  // 用量


    private int copperQuantity;  // 铜材数量


    private int orderQuantity;  // 订单数量


    private int plannedQuantity;  // 计划数量

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate feedingDate;  // 投料日期

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate machiningDeliveryDate;  // 机加交期

    private String manufacturingType;  // 自制/外发 (Enum 类型)

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;  // 下单日期

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate onlineDate;  // 上线日期

    private String orderStatus;  // 订单状态

    private String remarks;  // 备注


}
