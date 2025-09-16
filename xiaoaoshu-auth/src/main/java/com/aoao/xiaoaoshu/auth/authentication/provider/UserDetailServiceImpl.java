package com.aoao.xiaoaoshu.auth.authentication.provider;

import com.aoao.framework.common.util.JsonUtil;
import com.aoao.framework.common.constant.RedisKeyConstants;
import com.aoao.framework.common.constant.RedisTimeConstants;
import com.aoao.xiaoaoshu.auth.domain.authoriztion.LoginUser;
import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import com.aoao.xiaoaoshu.auth.rpc.UserRpcService;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByPhoneRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserRoleByPhoneRspDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
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
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserRpcService userRpcService;


    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        // 1.根据username从数据库中获取用户实例
        FindUserByPhoneRspDTO user = userRpcService.findUserByPhone(phone);
        // 2.判断用户是否存在
        if(Objects.isNull(user)){
            throw new UsernameNotFoundException(phone);
        }
        // 3.获取权限集合
        List<FindUserRoleByPhoneRspDTO> findUserRoleByPhoneRspDTOS = userRpcService.findUserRoleByPhone(phone);
        List<String> roles = new ArrayList<>();
        findUserRoleByPhoneRspDTOS.stream().forEach(findUserRoleByPhoneRspDTO -> {roles.add(findUserRoleByPhoneRspDTO.getRoleKey());});
        // 4.把用户-角色存入redis中
        String userRolesKey = RedisKeyConstants.buildUserRoleKey(user.getId().toString());
        stringRedisTemplate.opsForValue().set(userRolesKey, JsonUtil.toJson(roles), RedisTimeConstants.LOGIN_USER_TTL, TimeUnit.SECONDS);
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(user, userDO);
        return new LoginUser(userDO,roles);
    }
}
