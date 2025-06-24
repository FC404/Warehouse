package com.fxm.warehouse.service.impl;


import com.fxm.warehouse.mapper.RegisterMapper;
import com.fxm.warehouse.pojo.dto.UserAddDTO;
import com.fxm.warehouse.pojo.entity.User;
import com.fxm.warehouse.pojo.vo.UserAddVO;
import com.fxm.warehouse.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private RegisterMapper registerMapper;

    /**
     * 添加用户
     *
     * @param userAddDTO
     * @return
     */
    @Override
    public UserAddVO add(UserAddDTO userAddDTO) {
        // 1. 检查用户名是否已存在
        if (registerMapper.existsByUsername(userAddDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        // 1. 设置用户信息
        User user = new User();
        user.setUsername(userAddDTO.getUsername());
        user.setPassword(userAddDTO.getPassword());
        user.setRole(userAddDTO.getRole());
        user.setPhone(userAddDTO.getPhone());
        user.setEmail(userAddDTO.getEmail());
        // MD5加密密码
        user.setPassword(DigestUtils.md5DigestAsHex(userAddDTO.getPassword().getBytes()));
        // 2. 保存用户
        registerMapper.insert(user);
        // 3. 返回结果
        UserAddVO vo = new UserAddVO();
        vo.setUsername(user.getUsername());
        return vo;
    }
}
