package com.fxm.warehouse.service.impl;

import com.fxm.warehouse.mapper.LoginMapper;
import com.fxm.warehouse.pojo.dto.UserLoginDTO;
import com.fxm.warehouse.pojo.vo.UserLoginVO;
import com.fxm.warehouse.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginMapper loginMapper;

    //用户登录 密码使用md5加密
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        String password = DigestUtils.md5DigestAsHex(userLoginDTO.getPassword().getBytes());
        userLoginDTO.setPassword(password);
        return loginMapper.login(userLoginDTO);
    }
}
