package com.fxm.warehouse.pojo.dto;

import lombok.Data;

@Data
public class UserLoginDTO {

    private String username;
    private String role;

    private String password;

}
