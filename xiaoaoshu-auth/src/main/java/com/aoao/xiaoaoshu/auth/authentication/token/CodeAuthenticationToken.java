package com.aoao.xiaoaoshu.auth.authentication.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

/**
 * @author aoao
 * @create 2025-08-24-17:38
 */
public class CodeAuthenticationToken extends AbstractAuthenticationToken {

    private Object phone;
    private Object code;

    public CodeAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.phone = principal;
        this.code = credentials;
        setAuthenticated(false);
    }

    public CodeAuthenticationToken(Object principal, Object credentials,
                                      Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.phone = principal;
        this.code = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return code;
    }

    @Override
    public Object getPrincipal() {
        return phone;
    }

}
