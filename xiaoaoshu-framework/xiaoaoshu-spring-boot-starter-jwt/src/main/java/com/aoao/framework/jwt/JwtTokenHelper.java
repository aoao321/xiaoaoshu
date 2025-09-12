package com.aoao.framework.jwt;

import com.aoao.framework.jwt.properties.JwtTokenProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;


import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

/**
 * @author aoao
 * @create 2025-08-23-22:36
 */
@Component
public class JwtTokenHelper {

    private final JwtTokenProperties jwtTokenProperties;
    private final Key key;
    private final JwtParser jwtParser;

    public JwtTokenHelper(JwtTokenProperties jwtTokenProperties) {
        this.jwtTokenProperties = jwtTokenProperties;

        // 解析 secret
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtTokenProperties.getSecret()));

        // 在构造器直接初始化 jwtParser
        this.jwtParser = Jwts.parserBuilder()
                .requireIssuer(jwtTokenProperties.getIssuer())
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(10)
                .build();
    }


    /**
     * 生成 Token
     *
     * @param username
     * @return
     */
    public String generateToken(String username) {
        LocalDateTime now = LocalDateTime.now();
        // Token 一个小时后失效
        LocalDateTime expireTime = now.plusMinutes(jwtTokenProperties.getTokenExpireTime());

        return Jwts.builder().setSubject(username)
                .setIssuer(jwtTokenProperties.getIssuer())
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expireTime.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 Token
     *
     * @param token
     * @return
     */
    public Jws<Claims> parseToken(String token) {
        return jwtParser.parseClaimsJws(token);
    }

    /**
     * 校验 Token 是否可用
     *
     * @param token
     * @return
     */
    public void validateToken(String token) {
        jwtParser.parseClaimsJws(token);
    }

    /**
     * 解析 Token 获取用户名
     *
     * @param token
     * @return
     */
    public String getIdByToken(String token) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            return username;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
