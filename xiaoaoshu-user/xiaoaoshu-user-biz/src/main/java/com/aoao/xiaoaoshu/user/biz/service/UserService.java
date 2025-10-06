package com.aoao.xiaoaoshu.user.biz.service;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.aoao.xiaoaoshu.user.model.dto.req.*;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindNoteCreatorByIdRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByIdRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByPhoneRspDTO;

import java.util.List;

/**
 * @author aoao
 * @create 2025-09-11-15:08
 */
public interface UserService {
    Result update(UpdateUserInfoReqVO updateUserInfoReqVO);

    Result<Long> register(RegisterUserReqDTO registerUserReqDTO);

    Result<FindUserByPhoneRspDTO> findByPhone(FindUserByPhoneReqDTO findUserByPhoneReqDTO);

    Result<FindUserByIdRspDTO> findById(FindUserByIdReqDTO findUserByIdReqDTO);

    Result<FindNoteCreatorByIdRspDTO> findNoteCreatorById(FindNoteCreatorByIdReqDTO findNoteCreatorByIdReqDTO);

    Result<List<FindNoteCreatorByIdRspDTO>> findNoteCreatorsByIds(FindNoteCreatorsByIdsReqDTO findNoteCreatorsByIdsReqDTO);
}
