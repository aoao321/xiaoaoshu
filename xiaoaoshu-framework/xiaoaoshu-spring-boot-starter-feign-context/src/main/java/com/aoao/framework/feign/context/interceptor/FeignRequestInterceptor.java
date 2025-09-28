package com.aoao.framework.feign.context.interceptor;


import com.aoao.framework.common.constant.GlobalConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * @author aoao
 * @create 2025-09-16-15:14
 */
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 透传 Authorization (JWT)
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader(GlobalConstants.AUTHORIZATION);
            if (authHeader != null) {
                requestTemplate.header(GlobalConstants.AUTHORIZATION, authHeader);
                log.info("########## feign 请求设置请求头 Authorization: {}", authHeader);
            }
        }
    }
}
