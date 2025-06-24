package com.fxm.warehouse.service.impl;

import com.fxm.warehouse.mapper.UserMapper;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.User;
import com.fxm.warehouse.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public PageBean<User> page(Integer pageNum, Integer pageSize, String username, String phone, String email, String role) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> orderList = userMapper.page(username,phone,email,role);
        PageInfo<User> pageInfo = new PageInfo<>(orderList);
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public boolean update(User user) {
        try {
            // 先检查用户是否存在
            Optional<User> existingUser = userMapper.findById(user.getId());
            if (existingUser.isPresent()) {
                User updatedUser = existingUser.get();
                // 这里可以选择性更新字段
                updatedUser.setUsername(user.getUsername());
                updatedUser.setPhone(user.getPhone());
                updatedUser.setEmail(user.getEmail());
                updatedUser.setRole(user.getRole());
                // MD5加密密码
                updatedUser.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
                // 如果有其他需要更新的字段，可以继续添加

                userMapper.save(updatedUser); // 保存更新后的用户
                return true;
            } else {
                return false; // 用户不存在，更新失败
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 发生异常，更新失败
        }
    }

    @Override
    public boolean delete(List<Long> ids) {
        try {
            // 根据传入的 ID 查询这些用户
            List<User> usersToDelete = userMapper.findAllById(ids);

            if (usersToDelete.isEmpty()) {
                return false; // 没有找到要删除的用户
            }
            // 直接传递 ID 列表进行批量删除
            userMapper.deleteAll(ids); // 批量删除用户
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 发生异常，删除失败
        }
    }

    @Override
    public long count() {
        return userMapper.countUsers();
    }


}
