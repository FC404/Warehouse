package com.fxm.warehouse.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// 1. 请求参数 DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddDTO {
    private String username;//用户名
    private String password;//密码
    private String phone;//电话
    private String email;//邮箱
    private String role;//角色
}
