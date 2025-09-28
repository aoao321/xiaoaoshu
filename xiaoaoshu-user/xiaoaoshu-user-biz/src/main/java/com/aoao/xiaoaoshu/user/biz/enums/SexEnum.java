package com.aoao.xiaoaoshu.user.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


@Getter
@AllArgsConstructor
public enum SexEnum {

    WOMAN(0),
    MAN(1);

    private final Integer value;

    // 传入的值是否合法
    public static boolean isValid(Integer value) {
        for (SexEnum loginTypeEnum : SexEnum.values()) {
            if (Objects.equals(value, loginTypeEnum.getValue())) {
                return true;
            }
        }
        return false;
    }

}

