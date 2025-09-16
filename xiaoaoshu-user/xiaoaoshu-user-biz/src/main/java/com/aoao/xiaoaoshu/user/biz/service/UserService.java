package com.aoao.xiaoaoshu.user.biz.service;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.model.dto.RegisterUserReqDTO;
import com.aoao.xiaoaoshu.user.biz.model.vo.UpdateUserInfoReqVO;

/**
 * @author aoao
 * @create 2025-09-11-15:08
 */
public interface UserService {
    Result update(UpdateUserInfoReqVO updateUserInfoReqVO);

    Result<Long> register(RegisterUserReqDTO registerUserReqDTO);
}
