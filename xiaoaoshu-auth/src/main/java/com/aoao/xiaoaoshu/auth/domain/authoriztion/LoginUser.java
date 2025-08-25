package com.aoao.xiaoaoshu.auth.domain.authoriztion;

import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aoao
 * @create 2025-08-23-21:28
 */
@Data
public class LoginUser implements UserDetails {



    private UserDO userDO;
    private List<String> permissions;

    @JsonIgnore
    private List<SimpleGrantedAuthority> authorities;


    public LoginUser(UserDO userDO) {
        this.userDO = userDO;
    }

    public LoginUser(UserDO userDO, List<String> permissions){
        this.userDO = userDO;
        this.permissions = permissions;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities != null) {
            return authorities;
        }
        // 转换为权限集合
        authorities = permissions
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission))
                .collect(Collectors.toList());

        return authorities;
    }

    @Override
    public String getPassword() {
        return userDO.getPassword();
    }

    @Override
    public String getUsername() {
        return userDO.getPhone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userDO.getStatus() != null && userDO.getStatus() == 0;
    }
}
