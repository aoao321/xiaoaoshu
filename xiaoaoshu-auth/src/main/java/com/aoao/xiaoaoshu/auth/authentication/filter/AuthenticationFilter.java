package com.aoao.xiaoaoshu.auth.authentication.filter;

import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.xiaoaoshu.auth.authentication.exception.UsernameOrPasswordNullException;
import com.aoao.xiaoaoshu.auth.authentication.token.CodeAuthenticationToken;
import com.aoao.xiaoaoshu.auth.enums.LoginTypeEnum;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

/**
 * @author aoao
 * @create 2025-08-24-17:34
 */
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    // 登录路径拦截
    public AuthenticationFilter() {
        super(new AntPathRequestMatcher("/user/login", "POST"));
    }

    /**
     * 构造authentication对象
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();
        // 解析提交的 JSON 数据
        JsonNode jsonNode = mapper.readTree(request.getInputStream());
        // 校验手机号
        String phone = jsonNode.hasNonNull("phone") ? jsonNode.get("phone").asText() : null;
        if (phone == null || phone.isBlank()) {
            throw new BizException(ResponseCodeEnum.USERNAME_OR_PWD_IS_NULL);
        }
        // 判断登录方式
        String loginType = jsonNode.get("type").asText();
        // 密码登录
        if (LoginTypeEnum.PASSWORD.equals(LoginTypeEnum.valueOf(loginType))) {
            String password = jsonNode.get("password").asText();
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(phone, password);
            return this.getAuthenticationManager().authenticate(token);
        // 验证码登录
        } else if (LoginTypeEnum.VERIFICATION_CODE.equals(LoginTypeEnum.valueOf(loginType))) {
            String code = jsonNode.get("code").asText();
            CodeAuthenticationToken token = new CodeAuthenticationToken(phone, code);
            return this.getAuthenticationManager().authenticate(token);
        } else {
            throw new UsernameOrPasswordNullException(ResponseCodeEnum.USERNAME_OR_PWD_IS_NULL.getErrorMessage());
        }

    }
}
