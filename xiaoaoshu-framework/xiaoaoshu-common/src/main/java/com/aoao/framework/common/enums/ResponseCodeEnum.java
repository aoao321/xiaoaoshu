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

    VERIFICATION_CODE_SEND_FREQUENTLY("AUTH-40000", "请求太频繁，请3分钟后再试"),
    VERIFICATION_CODE_USELESS("AUTH-40001", "验证码不存在或已过期"),
    VERIFICATION_CODE_ERROR("AUTH-40002", "验证码错误"),
    PASSWORD_ERROR("AUTH-50000", "密码或验证码错误"),
    USERNAME_OR_PWD_IS_NULL("AUTH-20003","用户名或密码为空"),
    LOGIN_FAIL("AUTH-40005", "登录失败"),
    FORBIDDEN("AUTH-50005", "权限不足"),
    UNAUTHORIZED("AUTH-20002","用户未授权"),
    PHONE_ERROR("AUTH-40010", "手机号格式不正确，必须是 11 位数字"),
    TYPE_ERROR("AUTH-40015", "登录方式错误"),


    NICK_NAME_VALID_FAIL("USER-20001", "昵称请设置2-24个字符，不能使用@《/等特殊字符"),
    XIAOAOSHU_ID_VALID_FAIL("USER-20002", "小哈书号请设置6-15个字符，仅可使用英文（必须）、数字、下划线"),
    SEX_VALID_FAIL("USER-20003", "性别错误"),
    INTRODUCTION_VALID_FAIL("USER-20004", "个人简介请设置1-100个字符"),
    UPLOAD_AVATAR_FAIL("USER-20005", "头像上传失败"),
    UPLOAD_BACKGROUND_IMG_FAIL("USER-20006", "背景图上传失败"),
    ABSENT_USER("USER-20000", "用户不存在"),

    NOTE_CONTENT_NOT_FOUND("KV-20000", "该笔记内容不存在"),

    NOTE_TYPE_ERROR("NOTE-20000", "未知的笔记类型"),
    NOTE_PUBLISH_FAIL("NOTE-20001", "笔记发布失败"),
    NOTE_NOT_FOUND("NOTE-20002", "笔记不存在"),
    NOTE_PRIVATE("NOTE-20003", "作者已将该笔记设置为仅自己可见"),
    NOTE_UPDATE_FAIL("NOTE-20004", "笔记更新失败"),
    NOTE_UPDATE_VISIBLE_TYPE_ERROR("NOTE-20005", "未知笔记可见范围"),
    NOTE_CANT_OPERATE("NOTE-20006", "您无法操作该笔记"),

    TOPIC_NOT_FOUND("TOPIC-20000","未知的话题类型")
    ;




    ResponseCodeEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;


}
