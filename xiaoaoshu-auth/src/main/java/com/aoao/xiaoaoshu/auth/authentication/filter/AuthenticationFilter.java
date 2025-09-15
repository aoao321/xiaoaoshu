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
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author aoao
 * @create 2025-08-24-17:34
 */
@Component
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    // 登录路径拦截
    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher("/login", "POST"));
        setAuthenticationManager(authenticationManager);
    }

    /**
     * 构造authentication对象
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(request.getInputStream());

            String phone = jsonNode.hasNonNull("phone") ? jsonNode.get("phone").asText() : null;
            if (phone == null || phone.isBlank()) {
                throw new BizException(ResponseCodeEnum.USERNAME_OR_PWD_IS_NULL);
            }

            // 手机号必须 11 位
            if (!phone.matches("^\\d{11}$")) {
                throw new BizException(ResponseCodeEnum.PHONE_ERROR); // 自定义错误码
            }

            String loginType = jsonNode.get("type").asText();
            LoginTypeEnum typeEnum = LoginTypeEnum.valueOf(Integer.valueOf(loginType));

            if (LoginTypeEnum.PASSWORD.equals(typeEnum)) {
                String password = jsonNode.get("password").asText();
                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(phone, password);
                return this.getAuthenticationManager().authenticate(token);

            } else if (LoginTypeEnum.VERIFICATION_CODE.equals(typeEnum)) {
                String code = jsonNode.get("code").asText();
                CodeAuthenticationToken token = new CodeAuthenticationToken(phone, code);
                return this.getAuthenticationManager().authenticate(token);

            } else {
                throw new BizException(ResponseCodeEnum.TYPE_ERROR);
            }

        } catch (BizException e) {
            // 关键：将 BizException 包装成 AuthenticationServiceException
            throw new org.springframework.security.authentication.AuthenticationServiceException(e.getErrorMessage(), e);
        }
    }
}
