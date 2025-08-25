package com.aoao.xiaoaoshu.auth.authentication.provider;

import com.aoao.xiaoaoshu.auth.domain.authoriztion.LoginUser;
import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import com.aoao.xiaoaoshu.auth.domain.mapper.RoleDOMapper;
import com.aoao.xiaoaoshu.auth.domain.mapper.UserDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author aoao
 * @create 2025-08-23-21:25
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private RoleDOMapper roleDOMapper;


    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        // 1.根据username从数据库中获取用户实例
        UserDO userDO = userDOMapper.getByPhone(phone);
        // 2.判断用户是否存在
        if(Objects.isNull(userDO)){
            throw new UsernameNotFoundException(phone);
        }
        // 3.获取权限集合
        List<String> list = roleDOMapper.findRoleByPhone(phone);
        return new LoginUser(userDO,list);
    }
}
