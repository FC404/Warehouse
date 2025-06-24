package com.fxm.warehouse.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.fxm.warehouse.pojo.entity.Result;
import com.fxm.warehouse.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtils jwtUtils;
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {

        // 1. 获取请求 URL
        String url = req.getRequestURL().toString();
        log.info("请求的 URL: {}", url);

        // 2. 判断是否是登录操作
        if (url.contains("login")||url.contains("/login")) {
            log.info("登录操作放行");
            return true;
        }

        // 3. 获取请求头中的令牌 (token)
        String jwt = req.getHeader("token");
        if (!StringUtils.hasLength(jwt)) {
            log.warn("请求头缺少 token，返回未登录状态");
            respondWithError(resp, "未登录，请先登录", HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            // 4. 解析令牌
            Claims claims = jwtUtils.parseJWT(jwt);
            String role = claims.get("role", String.class); // 获取角色信息
            log.info("解析到的用户角色: {}", role);

            // 5. 判断管理员权限
            if (req.getRequestURI().startsWith("/*") && !"admin".equals(role)) {
                log.warn("非管理员用户尝试访问管理员资源，拒绝访问");
                respondWithError(resp, "无权限访问该资源", HttpServletResponse.SC_FORBIDDEN);
                return false;
            }

        } catch (Exception e) {
            log.error("解析 token 失败: {}", e.getMessage());
            respondWithError(resp, "无效的令牌，请重新登录", HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 6. 令牌合法，放行
        log.info("令牌合法，放行请求");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.debug("postHandle ...");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.debug("afterCompletion ...");
    }

    // 封装错误响应
    private void respondWithError(HttpServletResponse resp, String message, int status) throws Exception {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        Result error = Result.error(message);
        resp.getWriter().write(JSONObject.toJSONString(error));
    }
}
