package com.aoao.xiaoaoshu.gateway.auth.filter;

import com.aoao.framework.common.constant.GlobalConstants;
import com.aoao.framework.common.constant.RedisKeyConstants;
import com.aoao.framework.common.result.Result;
import com.aoao.framework.common.util.JsonUtil;
import com.aoao.framework.jwt.JwtTokenHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author aoao
 * @create 2025-09-04-17:17
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private final List<String> WHITE_LIST = Arrays.asList(
            "/auth/user/login",          // 登录接口（核心，必须加）
            "/auth/verification/code/send" // 发送验证码接口（如果有）
    );
    // 路径匹配器（用于判断请求路径是否命中白名单）
    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst("Authorization");
        String requestPath = request.getPath().value();

        for (String whitePath : WHITE_LIST) {
            // 支持通配符（如 /public/** 匹配 /public/xxx/yyy）
            if (pathMatcher.match(whitePath, requestPath)) {
                return chain.filter(exchange); // 直接放行，进入下一个过滤器/转发到服务
            }
        }

        if (StringUtils.isBlank(token) || !token.startsWith("Bearer ")) {
            return unauthorized(exchange, "缺少或非法的Token");
        }
        token = token.substring(7);

        // 校验 token 是否过期 / 是否被踢下线
        try {
            jwtTokenHelper.validateToken(token);

            String userId = jwtTokenHelper.getIdByToken(token);

            String redisToken = redisTemplate.opsForValue().get(RedisKeyConstants.buildTokenKey(Long.valueOf(userId)));
            if (!token.equals(redisToken)) {
                return unauthorized(exchange, "Token已失效");
            }
        } catch (Exception e) {
            return unauthorized(exchange, "Token非法或已过期");
        }

        String id = jwtTokenHelper.getIdByToken(token);
        String key = RedisKeyConstants.buildUserRoleKey(id);

        // 获取redis中用户的角色
        String roleListStr = redisTemplate.opsForValue().get(key);
        // 解析为list
        List<String> roleList = JsonUtil.fromJson(roleListStr, List.class);
        // 遍历角色，获取权限
        Set<String> permissions = new HashSet<>();
        for (String role : roleList) {
            String permKey = RedisKeyConstants.buildRolePermissionsKey(role);
            String permListStr = redisTemplate.opsForValue().get(permKey);
            if (StringUtils.isNotBlank(permListStr)) {
                List<String> permList = JsonUtil.fromJson(permListStr, List.class);
                permissions.addAll(permList);
            }
        }

        // 传递给下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(GlobalConstants.AUTHORIZATION, "Bearer " + token)
                .header(GlobalConstants.USER_ID, id)
                .header(GlobalConstants.USER_ROLES, String.join(",", roleList))
                .header(GlobalConstants.USER_PERMISSIONS, String.join(",", permissions))
                .build();


        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        ServerHttpResponse response = exchange.getResponse();
        // 设置响应为JSON格式
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        // 构造统一JSON响应（示例：与你的Result类格式匹配）
        String json = "{\"code\":401,\"message\":\"" + msg + "\",\"data\":null}";
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
