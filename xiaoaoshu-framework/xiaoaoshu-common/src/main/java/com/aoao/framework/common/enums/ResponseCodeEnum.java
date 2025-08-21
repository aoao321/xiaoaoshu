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

    PARAM_NOT_VALID("10001","参数验证错误"),;

    ResponseCodeEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;


}
