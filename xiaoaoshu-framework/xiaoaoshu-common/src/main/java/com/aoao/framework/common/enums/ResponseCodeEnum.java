package com.aoao.framework.common.enums;

import lombok.Getter;

/**
 * @author aoao
 * @create 2025-07-11-9:27
 */
@Getter
public enum ResponseCodeEnum {

    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "出错啦，后台小哥正在努力修复中..."),

    // ----------- 业务异常状态码 -----------
    PRODUCT_NOT_FOUND("20000", "该产品不存在（测试使用）"),

    PARAM_NOT_VALID("10001","参数验证错误"),

    VERIFICATION_CODE_SEND_FREQUENTLY("40000", "请求太频繁，请3分钟后再试"),
    VERIFICATION_CODE_USELESS("40001", "验证码不存在或已过期"),
    VERIFICATION_CODE_ERROR("40002", "验证码错误"),
    PASSWORD_ERROR("50000", "密码或验证码错误"),
    USERNAME_OR_PWD_IS_NULL("20003","用户名或密码为空"),
    LOGIN_FAIL("40005", "登录失败"),
    FORBIDDEN("50005", "权限不足"),
    UNAUTHORIZED("20002","用户未授权"),
    PHONE_ERROR("40010", "手机号格式不正确，必须是 11 位数字"),
    TYPE_ERROR("40015", "登录方式错误");

    ResponseCodeEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;


}
