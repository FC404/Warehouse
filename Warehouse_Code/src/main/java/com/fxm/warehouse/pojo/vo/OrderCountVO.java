package com.fxm.warehouse.pojo.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCountVO {

    private String orderDate;
    private Long orderCount;
}
