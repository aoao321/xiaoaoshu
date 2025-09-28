package com.aoao.xiaoaoshu.auth.interceptor;

import com.aoao.framework.common.constant.GlobalConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import static com.aoao.framework.common.constant.GlobalConstants.INTERNAL_TOKEN;

/**
 * 内部服务调用 Feign 拦截器
 * 在请求头里加上一个内部约定的 Token，用于 user 等下游服务识别
 *
 * @author aoao
 * @create 2025-09-27-11:10
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 添加内部服务 token
        requestTemplate.header(GlobalConstants.INTERNAL_HEADER, GlobalConstants.INTERNAL_TOKEN);
    }
}
