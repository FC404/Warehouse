package com.fxm.warehouse.controller;


import com.fxm.warehouse.pojo.entity.Result;
import com.fxm.warehouse.pojo.dto.UserLoginDTO;
import com.fxm.warehouse.pojo.entity.User;
import com.fxm.warehouse.pojo.vo.UserLoginVO;
import com.fxm.warehouse.service.LoginService;
import com.fxm.warehouse.service.UserService;
import com.fxm.warehouse.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private JwtUtils jwtUtils;
    // redis缓存
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("登录:{}", userLoginDTO);
        UserLoginVO u = loginService.login(userLoginDTO);
        //登录成功，生成令牌 下发令牌
        if (u != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", u.getUsername());
            claims.put("role", u.getRole());
            String token = jwtUtils.generateJwt(claims); //jwt当前用户信息
            log.info(token);
            //登录成功，存一份toke到redis中,且设置过期时间和token过期时间一致12小时主动失效
            stringRedisTemplate.opsForValue().set(token, token, 12, TimeUnit.HOURS);

            return Result.success(token);
        }
        //登录失败 返回错误信息
        return Result.error("用户名或密码错误");
    }

    @DeleteMapping("/logout")
    public Result logout(HttpServletRequest request) {
        // 获取 Authorization 请求头中的 Token（Bearer <token> 格式）
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;

        // 处理 Authorization 请求头
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);  // 去掉 Bearer 前缀
        } else {
            // 如果没有 Authorization 头，则直接尝试获取 token 请求头
            token = request.getHeader("token");
        }

        log.info("Received Token: {}", token);

        if (token == null || token.isEmpty()) {
            return Result.error("Token 不存在，退出失败");
        }
        try {
            // 解析 token，获取用户名（如果需要的话）
            String username = jwtUtils.getUsernameFromToken(token);
            // 删除 Redis 中的 token，使其失效
            stringRedisTemplate.delete(token);
            // 返回退出成功
            return Result.success("退出成功");
        } catch (Exception e) {
            // Token 解析失败，返回错误信息
            log.error("Token 解析失败，退出失败", e);
            return Result.error("Token 解析失败，退出失败");
        }
    }

    @PostMapping("/validate-user")
    public Result validateUser(@RequestHeader("token") String token) {
        // 如果 token 为 null 或者空字符串，返回验证失败
        if (token == null || token.isEmpty()) {
            return Result.error("Token 不存在，验证失败");
        }
        try {
            // 解析 token
            Claims claims = jwtUtils.parseJWT(token);

            // 如果需要，获取其他字段（例如用户名）
            String username = claims.get("username", String.class);  // 这里只是示例，可以根据需要获取其他字段
            String role = claims.get("role", String.class);
            log.info("Token 验证成功，用户：{}", username,role);

            // 返回成功结果
            return Result.success("Token 验证成功");

        } catch (Exception e) {
            // Token 解析失败，返回错误信息
            log.error("Token 解析失败，验证失败", e);
            return Result.error("Token 无效或解析失败");
        }
    }

    @PostMapping("/validate-role")
    public Result validateRole(@RequestHeader("token") String token) {
        // 如果 token 为 null 或者空字符串，返回验证失败
        if (token == null || token.isEmpty()) {
            return Result.error("Token 不存在，验证失败");
        }
        try {
            // 解析 token
            Claims claims = jwtUtils.parseJWT(token);
            String role = claims.get("role", String.class);
            log.info("Token 验证成功，用户：{}", role);
            // 返回成功结果
            return Result.success(role);

        } catch (Exception e) {
            // Token 解析失败，返回错误信息
            log.error("Token 解析失败，验证失败", e);
            return Result.error("Token 无效或解析失败");
        }
    }

    @GetMapping("/get-user-info")
    public Result getUserInfo(@RequestHeader("token") String token) {
        System.out.println("Received token: " + token);  // 打印接收到的 token

        try {
            String username = jwtUtils.getUsernameFromToken(token);  // 解析 token
            return Result.success(username);  // 返回用户名
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("Token 无效或解析失败");
        }
    }


}
