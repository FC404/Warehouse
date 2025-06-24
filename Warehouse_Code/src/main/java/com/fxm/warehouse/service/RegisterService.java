package com.fxm.warehouse.service;


import com.fxm.warehouse.pojo.dto.UserAddDTO;
import com.fxm.warehouse.pojo.vo.UserAddVO;

public interface RegisterService {
    /**
     * 注册用户
     * @param userAddDTO
     * @return
     */
    UserAddVO add(UserAddDTO userAddDTO);
}
