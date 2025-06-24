package com.fxm.warehouse.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String signKey;

    @Value("${jwt.expire}")
    private Long expire;


    public String generateJwt(Map<String, Object> claims) {
        if (signKey == null || signKey.isEmpty()) {
            throw new IllegalArgumentException("JWT 密钥不能为空！");
        }
        System.out.println("JWT Secret Key: " + signKey);
        // 添加 jti 字段（JWT ID）
        claims.put("jti", UUID.randomUUID().toString());
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey.getBytes())  // 确保用字节数组
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
    }


    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public Claims parseJWT(String jwt) {
        if (signKey == null || signKey.isEmpty()) {
            throw new IllegalArgumentException("JWT 密钥不能为空！");
        }

        try {
            // 使用字节数组作为密钥来解析
            Claims claims = Jwts.parser()
                    .setSigningKey(signKey.getBytes())  // 修改为字节数组
                    .parseClaimsJws(jwt)
                    .getBody();
            return claims;
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT Token 已过期");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("无效的 JWT 格式");
        } catch (SignatureException e) {
            throw new RuntimeException("JWT 签名无效");
        } catch (Exception e) {
            throw new RuntimeException("JWT 解析失败", e);  // 输出详细的异常信息
        }
    }

    /**
     * 从JWT中获取用户名
     * @param jwt JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.get("username", String.class);  // 获取 "username" 字段
    }

    /**
     * 校验JWT是否过期
     * @param jwt JWT令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String jwt) {
        Claims claims = parseJWT(jwt);
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
