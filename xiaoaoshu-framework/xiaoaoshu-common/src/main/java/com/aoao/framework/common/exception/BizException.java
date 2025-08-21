package com.aoao.framework.common.exception;

import com.aoao.framework.common.enums.ResponseCodeEnum;

/**
 * @author aoao
 * @create 2025-08-18-21:28
 */
public class BizException extends BaseException {

    public BizException(ResponseCodeEnum codeEnum) {
        this.setErrorCode(codeEnum.getErrorCode());
        this.setErrorMessage(codeEnum.getErrorMessage());
    }

}
