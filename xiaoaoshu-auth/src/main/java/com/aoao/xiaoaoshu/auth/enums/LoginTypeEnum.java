package com.aoao.xiaoaoshu.auth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author aoao
 * @create 2025-08-24-16:44
 */
@Getter

public enum LoginTypeEnum {

    // 验证码
    VERIFICATION_CODE(1),
    // 密码
    PASSWORD(2);

    public static LoginTypeEnum valueOf(Integer code) {
        for (LoginTypeEnum loginTypeEnum : LoginTypeEnum.values()) {
            if (Objects.equals(code, loginTypeEnum.getValue())) {
                return loginTypeEnum;
            }
        }
        return null;
    }

    LoginTypeEnum(Integer value) {
        this.value = value;
    }

    private final Integer value;

}
