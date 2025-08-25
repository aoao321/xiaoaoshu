package com.aoao.framework.common.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author aoao
 * @create 2025-08-18-21:24
 */
@Getter
@Setter
public abstract class BaseException extends RuntimeException {

  // 异常码
  private String errorCode;
  // 错误信息
  private String errorMessage;

  public BaseException(String message) {
    super(message); // 调用 RuntimeException 的构造器
  }

  public BaseException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
    this.errorMessage = message;
  }

}
