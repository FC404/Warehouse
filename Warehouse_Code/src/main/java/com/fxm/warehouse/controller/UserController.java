package com.fxm.warehouse.controller;


import com.fxm.warehouse.anno.Log;
import com.fxm.warehouse.pojo.entity.Order;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.Result;
import com.fxm.warehouse.pojo.entity.User;
import com.fxm.warehouse.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     *
     * @param pageNum  当前页
     * @param pageSize 每页大小
     * @return 分页数据
     */
    @GetMapping("/list")
    public Result<PageBean<User>> listUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role
    ) {
        // 调用 service 进行查询
        PageBean<User> pageBean = userService.page(pageNum, pageSize,username,phone,email,role);
        return Result.success(pageBean);
    }


    /**
     * 修改用户信息
     */
    @Log
    @PutMapping("/update")
    public Result<Void> updateUser(@RequestBody User user) {
        boolean updated = userService.update(user);
        return updated ? Result.success() : Result.error("修改失败");
    }

    /**
     * 删除用户（支持单个和批量删除）
     */
    @Log
    @DeleteMapping("/delete")
    public Result<Void> deleteUser(@RequestBody Map<String, List<Long>> requestBody) {
        List<Long> ids = requestBody.get("ids");
        boolean deleted = userService.delete(ids);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    /**
     * 统计用户总数
     */
    @GetMapping("/count")
    public Result<Long> countUsers() {
        long userCount = userService.count();
        return Result.success(userCount);
    }

}
