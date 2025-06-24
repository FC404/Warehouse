package com.fxm.warehouse.aop;

import com.alibaba.fastjson.JSONObject;
import com.fxm.warehouse.mapper.OperateLogMapper;
import com.fxm.warehouse.pojo.entity.OperateLog;
import com.fxm.warehouse.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Component
@Aspect
public class LogAspect {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private OperateLogMapper operateLogMapper;

    @Around("@annotation(com.fxm.warehouse.anno.Log)")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求头中的jwt令牌
        String jwt = request.getHeader("token");

        // 打印当前token值，用于调试
        log.info("Current token: {}", jwt);

        String operateUser = "";

        // 添加 token 验证
        if (jwt != null && !jwt.trim().isEmpty()) {
            try {
                Claims claims = jwtUtils.parseJWT(jwt);
                operateUser = (String) claims.get("username");
                log.info("operateUser: {}", operateUser);
            } catch (Exception e) {
                log.error("Token解析失败: {}", e.getMessage());
            }
        } else {
            log.warn("Token为空");
        }

        // 操作时间
        LocalDateTime operateTime = LocalDateTime.now();

        // 操作类名
        String className = joinPoint.getTarget().getClass().getName();
        // 操作方法名
        String methodName = joinPoint.getSignature().getName();

        // 操作方法参数
        Object[] args = joinPoint.getArgs();
        String methodParms = Arrays.toString(args);

        long begin = System.currentTimeMillis();
        // 调用原始目标方法运行
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        // 方法返回值
        String returnValue = JSONObject.toJSONString(result);
        // 操作耗时
        Long costTime = end - begin;

        // 记录操作日志
        OperateLog operateLog = new OperateLog(null, operateUser, operateTime,
                className, methodName, methodParms, returnValue, costTime);
        operateLogMapper.insert(operateLog);

        log.info("AOP记录日志: {}", operateLog);
        return result;
    }
}