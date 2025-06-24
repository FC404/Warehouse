package com.fxm.warehouse.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutsourcingDelivery {

    private Long id;  // 主键，自动递增

    private String supplier;  // 供应商

    private String productCode;  // 产品编码

    private String productName;  // 产品名称

    private int outsourceQuantity;  // 外发数量
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate outsourceDate;  // 外发日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate plannedCompletionDate;  // 计划完工日期
    private String isOnline;  // 是否上线，状态：上线/未上线
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;  // 回厂日期

    private int goodQuantity;  // 良品数量

    private String remarks;  // 备注

}
