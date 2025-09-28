package com.aoao.xiaoaoshu.user.api;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.constant.ApiConstants;
import com.aoao.xiaoaoshu.user.model.dto.req.*;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindNoteCreatorByIdRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByIdRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByPhoneRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserRoleByPhoneRspDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author aoao
 * @create 2025-09-16-16:47
 */
@FeignClient(name = ApiConstants.SERVICE_NAME)
public interface UserFeignApi {
    String PREFIX = "/user";

    @PostMapping(value = PREFIX + "/findById")
    Result<FindUserByIdRspDTO> findById(@RequestBody FindUserByIdReqDTO findUserByIdReqDTO);

    @PostMapping(value = PREFIX + "/register")
    Result<Long> registerUser(@RequestBody RegisterUserReqDTO registerUserReqDTO);

    @PostMapping(value = PREFIX + "/findByPhone")
    Result<FindUserByPhoneRspDTO> findUserByPhone(@RequestBody FindUserByPhoneReqDTO findUserByPhoneReqDTO);

    @PostMapping(value = PREFIX + "/role/findRoleByPhone")
    Result<List<FindUserRoleByPhoneRspDTO>> findUserRoleByPhone(@RequestBody FindUserRoleByPhoneReqDTO findUserRoleByPhoneReqDTO);

    @PostMapping(value = PREFIX + "/findNoteCreatorById")
    Result<FindNoteCreatorByIdRspDTO> findNoteCreatorById(@RequestBody FindNoteCreatorByIdReqDTO findNoteCreatorByIdReqDTO);
}
