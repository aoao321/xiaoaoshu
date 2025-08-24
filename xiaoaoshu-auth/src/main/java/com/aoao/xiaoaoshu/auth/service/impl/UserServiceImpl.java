package com.aoao.xiaoaoshu.auth.service.impl;

import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.auth.constant.RedisKeyConstants;
import com.aoao.xiaoaoshu.auth.domain.authoriztion.LoginUser;
import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import com.aoao.xiaoaoshu.auth.domain.mapper.UserDOMapper;
import com.aoao.xiaoaoshu.auth.enums.LoginTypeEnum;
import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.xiaoaoshu.auth.model.vo.user.UserLoginReqVO;
import com.aoao.xiaoaoshu.auth.service.UserService;
import com.aoao.xiaoaoshu.auth.util.JwtTokenHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author aoao
 * @create 2025-08-24-14:42
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String token = "";
    private UserDO userDO = null;

    @Override
    public Result login(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        Integer type = userLoginReqVO.getType();

        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(type);
        switch (loginTypeEnum) {
            case VERIFICATION_CODE: // 验证码登录
                String verificationCode = userLoginReqVO.getCode();
                // 校验入参验证码是否为空
                if (StringUtils.isBlank(verificationCode)) {
                    return Result.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "验证码不能为空");
                }
                // 从redis中取出该手机号的验证码
                String key = RedisKeyConstants.buildVerificationCodeKey(phone);
                String sentCode = (String) stringRedisTemplate.opsForValue().get(key);
                if (sentCode == null || sentCode.isEmpty()) {
                    throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_USELESS);
                }
                if (!sentCode.equals(verificationCode)) {
                    throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_ERROR);
                }
                // 验证通过后删除
                stringRedisTemplate.delete(key);
                // 通过手机号查询记录
                userDO = userDOMapper.getByPhone(phone);
                // 不存在则注册新用户
                if (Objects.isNull(userDO)) {
                    registerUser(userLoginReqVO);
                    userDO = userDOMapper.getByPhone(phone);
                }
                // 存在返回token
                Long id = userDO.getId();
                token = jwtTokenHelper.generateToken(id.toString());
                break;
            case PASSWORD:
                String password = userLoginReqVO.getPassword();
                // 调用security认证链
                UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(phone, password);
                // 使用authenticationManager执行密码认证

                Authentication authenticate = authenticationManager.authenticate(authenticationToken);
                // 登录成功，获取认证的用户
                LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
                userDO = loginUser.getUserDO(); // 直接从 UserDetails 获取
                token = jwtTokenHelper.generateToken(userDO.getId().toString());
                break;
            default:
                break;
        }
        return Result.success(token);
    }

    private void registerUser(UserLoginReqVO userLoginReqVO) {

    }
}
