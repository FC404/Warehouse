package com.fxm.warehouse.pojo.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 2. 响应数据 VO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddVO {

    private String username;//用户名
    private String password;//密码
    private String phone;//电话
    private String email;//邮箱
    private String role;//角色

}
