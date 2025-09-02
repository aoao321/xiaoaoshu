package com.aoao.xiaoaoshu.auth.authentication.provider;

import com.aoao.framework.common.util.JsonUtil;
import com.aoao.xiaoaoshu.auth.constant.RedisKeyConstants;
import com.aoao.xiaoaoshu.auth.constant.RedisTimeConstants;
import com.aoao.xiaoaoshu.auth.domain.authoriztion.LoginUser;
import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import com.aoao.xiaoaoshu.auth.domain.mapper.RoleDOMapper;
import com.aoao.xiaoaoshu.auth.domain.mapper.UserDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        // 1.根据username从数据库中获取用户实例
        UserDO userDO = userDOMapper.getByPhone(phone);
        // 2.判断用户是否存在
        if(Objects.isNull(userDO)){
            throw new UsernameNotFoundException(phone);
        }
        // 3.获取权限集合
        List<String> roles = roleDOMapper.findRoleByPhone(phone);
        // 4.把用户-角色存入redis中
        String userRolesKey = RedisKeyConstants.buildUserRoleKey(phone);
        stringRedisTemplate.opsForValue().set(userRolesKey, JsonUtil.toJson(roles), RedisTimeConstants.LOGIN_USER_TTL, TimeUnit.SECONDS);
        return new LoginUser(userDO,roles);
    }
}
