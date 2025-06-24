package com.fxm.warehouse.service;

import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);

    PageBean<User> page(Integer pageNum, Integer pageSize, String username, String phone, String email, String role);

    boolean update(User user);

    boolean delete(List<Long> ids);

    long count();
}
