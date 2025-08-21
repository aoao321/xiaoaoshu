package com.aoao.framework.common.result;

import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author aoao
 * @create 2025-08-18-21:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class  Result<T> implements Serializable {
    // 响应是否成功，成功为true
    private boolean success=true;
    // 数据
    private T data;
    // 错误代码
    private String errorCode;
    // 错误信息
    private String message;

    public static <T> Result<T> success() {return new Result<>(true,null,null,null);}

    public static <T> Result<T> success(T data) {
        return new Result<>(true,data,null,null);
    }

    public static <T> Result<T> fail( String errorMsg) {
        return new Result<>(false,null,null,errorMsg);
    }

    public static <T> Result<T> fail(String errorCode, String errorMsg) {
        return new Result<>(false,null,errorCode,errorMsg);
    }

    public static <T> Result<T> fail(BaseException baseException) {
        return new Result<>(false,null,baseException.getErrorCode(),baseException.getErrorMessage());
    }

    public static <T> Result<T> fail(ResponseCodeEnum responseCodeEnum) {
        return new Result<>(false,null,responseCodeEnum.getErrorCode(), responseCodeEnum.getErrorMessage());
    }

}
