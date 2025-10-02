package com.aoao.xiaoaoshu.note.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum NoteVisibleEnum {

    PUBLIC(0), // 公开，所有人可见
    PRIVATE(1); // 仅自己可见

    private final Integer code;

    public static boolean isValid(Integer code) {
        for (NoteVisibleEnum e : NoteVisibleEnum.values()) {
            if (Objects.equals(code, e.getCode())) {
                return true;
            }
        }
        return false;
    }

    public static NoteVisibleEnum valueOf(Integer code) {
        for (NoteVisibleEnum e : NoteVisibleEnum.values()) {
            if (Objects.equals(code, e.getCode())) {
                return e;
            }
        }
        return null;
    }

}

