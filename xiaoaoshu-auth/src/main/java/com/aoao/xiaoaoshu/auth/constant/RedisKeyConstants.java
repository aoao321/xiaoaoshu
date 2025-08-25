package com.aoao.xiaoaoshu.auth.constant;

/**
 * @author aoao
 * @create 2025-08-22-19:08
 */
public class RedisKeyConstants {

    /**
     * 验证码 KEY 前缀
     */
    private static final String VERIFICATION_CODE_KEY_PREFIX = "verification_code:";

    /**
     * 构建验证码 KEY
     * @param phone
     * @return
     */
    public static String buildVerificationCodeKey(String phone) {
        return VERIFICATION_CODE_KEY_PREFIX + phone;
    }


    /**
     * token KEY前缀
     */
    private static final String TOKEN_KEY_PREFIX = "token:";

    /**
     * 构建token KEY
     */
    public static String buildTokenKey(Long userId) {
        return TOKEN_KEY_PREFIX + userId;

    }

    /**
     * 自增长id KEY
     */
    public static final String XIAOAOSHU_ID_GENERATOR_KEY = "xiaoaoshu_id_generator";
}

