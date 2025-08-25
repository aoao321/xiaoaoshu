package com.aoao.xiaoaoshu.auth.util;

import com.aoao.xiaoaoshu.auth.properties.JwtTokenProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
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
public class JwtTokenHelper implements InitializingBean {

    @Autowired
    private JwtTokenProperties jwtTokenProperties;

    /**
     * 秘钥
     */
    private Key key;

    /**
     * JWT 解析
     */
    private JwtParser jwtParser;

    /**
     * 解码配置文件中配置的 Base 64 编码 key 为秘钥
     * @param base64Key
     */
    @Value("${jwt.secret}")
    public void setBase64Key(String base64Key) {
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
    }


    /**
     * 初始化 JwtParser
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 考虑到不同服务器之间可能存在时钟偏移，setAllowedClockSkewSeconds 用于设置能够容忍的最大的时钟误差
        jwtParser = Jwts.parserBuilder().requireIssuer(jwtTokenProperties.getIssuer())
                .setSigningKey(key).setAllowedClockSkewSeconds(10)
                .build();
    }

    /**
     * 生成 Token
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
     * @param token
     * @return
     */
    public Jws<Claims> parseToken(String token) {
        try {
            return jwtParser.parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("Token 失效", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("Token 不可用", e);
        }
    }

    /**
     * 校验 Token 是否可用
     * @param token
     * @return
     */
    public void validateToken(String token) {
        jwtParser.parseClaimsJws(token);
    }

    /**
     * 解析 Token 获取用户名
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
