package com.aoao.xiaoaoshu.auth.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.auth.constant.RedisKeyConstants;
import com.aoao.xiaoaoshu.auth.model.vo.verificationcode.SendVerificationCodeReqVO;
import com.aoao.xiaoaoshu.auth.service.VerificationCodeService;
import com.aoao.xiaoaoshu.auth.sms.AliSmsSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @author aoao
 * @create 2025-08-22-19:12
 */
@Service
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AliSmsSender aliSmsSender;

    @Override
    public Result<?> send(SendVerificationCodeReqVO sendVerificationCodeReqVO) {
        String phone = sendVerificationCodeReqVO.getPhone();
        // 根据手机号查询redis中是否已存在该数据
        String key = RedisKeyConstants.buildVerificationCodeKey(phone);
        String string = stringRedisTemplate.opsForValue().get(key);
        // 存在,提示等待一分钟
        if (StringUtils.isNotBlank(string)) {
            throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_SEND_FREQUENTLY);
        }
        // 生成验证码
        String code = RandomUtil.randomNumbers(4);
        // 存入redis中，设置过期时间1分钟
        stringRedisTemplate.opsForValue().set(key,code,1, TimeUnit.MINUTES);
        // TODO: 发短信
        aliSmsSender.sendVerificationCode(phone,code);
        return Result.success();
    }
}
