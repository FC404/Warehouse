package com.fxm.warehouse.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Integer id;
    private String customer;//客户

    private String orderNumber;//订单号

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    private String customerPartNumber;

    private String factoryPartNumber;

    private Integer orderQuantity;

    private Integer deliveredQuantity;

    private String status;

    private String remarks;

    private String productName; // 产品名称

    private String materialProgress; // 投料进度（已投料 / 未投料）

    private String materialshortagedetails;//欠料明细

    private String materialPreparationProgress; // 备料进度（已备料 / 待备料）

    private String installationProgress; // 安装进度（已上线 / 待上线）

    private String outsourcing;//外发进度 （已外发 / 待外发）
}
