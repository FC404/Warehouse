package com.fxm.warehouse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User  {

    private Long id; // 改为 Long 类型
    private String username;//用户名
    private String password;//密码
    private String phone;//电话
    private String email;//邮箱
    private String role;//角色

}
