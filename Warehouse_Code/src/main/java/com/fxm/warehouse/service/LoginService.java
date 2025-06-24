package com.fxm.warehouse.service;


import com.fxm.warehouse.pojo.dto.UserLoginDTO;
import com.fxm.warehouse.pojo.vo.UserLoginVO;

public interface LoginService {

    /**
     * 登录
     * @param userLoginDTO
     * @return
     */
    UserLoginVO login(UserLoginDTO userLoginDTO);
}
