package com.fxm.warehouse.filter;

import com.alibaba.fastjson.JSONObject;
import com.fxm.warehouse.pojo.entity.Result;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        // 1. 获取请求 URL
        String url = req.getRequestURL().toString();
        log.info("请求的url:{}", url);

        // 2. 放行登录和注册请求
        if (url.contains("/login") || url.contains("/register")) {
            log.info("登录注册操作放行");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 3. 处理 CORS 预检请求（OPTIONS 请求）
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            log.info("OPTIONS 预检请求，直接放行");
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 4. 获取请求头中的 Token
        String jwt = req.getHeader("token");

        // 5. 判断令牌是否存在
        if (!StringUtils.hasLength(jwt)) {
            log.info("请求头 token 为空，返回未登录的信息");
            sendErrorResponse(resp, "未登录");
            return;
        }


        // 8. 令牌合法，放行
        log.info("令牌合法，放行");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 发送 JSON 格式的错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Result error = Result.error(message);
        response.getWriter().write(JSONObject.toJSONString(error));
    }

}
