package com.aoao.xiaoaoshu.auth.service;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.auth.model.vo.verificationcode.SendVerificationCodeReqVO;

/**
 * @author aoao
 * @create 2025-08-22-19:12
 */
public interface VerificationCodeService {
    Result<?> send(SendVerificationCodeReqVO sendVerificationCodeReqVO);
}
