package com.fxm.warehouse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageBean<T> {
    private Long total;//总记录数
    private List<T> rows;//数据列表
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页记录数

}