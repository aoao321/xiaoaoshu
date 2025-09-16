package com.aoao.xiaoaoshu.auth.rpc;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.api.UserFeignApi;
import com.aoao.xiaoaoshu.user.model.dto.req.FindUserByIdReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.req.FindUserByPhoneReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.req.FindUserRoleByPhoneReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.req.RegisterUserReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByIdRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByPhoneRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserRoleByPhoneRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author aoao
 * @create 2025-09-16-19:00
 */
@Component
public class UserRpcService {

    @Autowired
    private UserFeignApi userFeignApi;

    public Long register(String phone) {
        RegisterUserReqDTO registerUserReqDTO = new RegisterUserReqDTO();
        registerUserReqDTO.setPhone(phone);

        Result<Long> response = userFeignApi.registerUser(registerUserReqDTO);

        if (!response.isSuccess()) {
            return null;
        }
        return response.getData();
    }

    public FindUserByPhoneRspDTO findUserByPhone(String phone) {
        FindUserByPhoneReqDTO findUserByPhoneReqDTO = new FindUserByPhoneReqDTO(phone);
        Result<FindUserByPhoneRspDTO> user = userFeignApi.findUserByPhone(findUserByPhoneReqDTO);
        if (!user.isSuccess()) {
            return null;
        }
        return user.getData();
    }

    public List<FindUserRoleByPhoneRspDTO> findUserRoleByPhone(String phone) {
        FindUserRoleByPhoneReqDTO findUserRoleByPhoneReqDTO = new FindUserRoleByPhoneReqDTO(phone);
        Result<List<FindUserRoleByPhoneRspDTO>> role = userFeignApi.findUserRoleByPhone(findUserRoleByPhoneReqDTO);
        if (!role.isSuccess()) {
            return null;
        }
        return role.getData();
    }

    public FindUserByIdRspDTO findUserById(Long id) {
        FindUserByIdReqDTO findUserByIdReqDTO = new FindUserByIdReqDTO(id);
        Result<FindUserByIdRspDTO> result = userFeignApi.findById(findUserByIdReqDTO);
        if (!result.isSuccess()) {
            return null;
        }
        return result.getData();
    }
}
