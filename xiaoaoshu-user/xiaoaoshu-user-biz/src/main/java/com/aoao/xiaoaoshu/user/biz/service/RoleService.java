package com.aoao.xiaoaoshu.user.biz.service;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.model.dto.req.FindUserRoleByPhoneReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserRoleByPhoneRspDTO;

import java.util.List;

/**
 * @author aoao
 * @create 2025-09-16-23:01
 */
public interface RoleService {

    Result<List<FindUserRoleByPhoneRspDTO>> findRoleByPhone(FindUserRoleByPhoneReqDTO findUserRoleByPhoneReqDTO);
}
