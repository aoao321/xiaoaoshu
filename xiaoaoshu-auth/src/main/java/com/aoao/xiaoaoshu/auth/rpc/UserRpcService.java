package com.aoao.xiaoaoshu.auth.rpc;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.api.UserFeignApi;
import com.aoao.xiaoaoshu.user.model.dto.RegisterUserReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author aoao
 * @create 2025-09-16-19:00
 */
@Component
public class UserRpcService {

    @Autowired
    private UserFeignApi userFeignApi;

    /**
     * 用户注册
     *
     * @param phone
     * @return
     */
    public Long register(String phone) {
        RegisterUserReqDTO registerUserReqDTO = new RegisterUserReqDTO();
        registerUserReqDTO.setPhone(phone);

        Result<Long> response = userFeignApi.registerUser(registerUserReqDTO);

        if (!response.isSuccess()) {
            return null;
        }
        return response.getData();
    }

}
