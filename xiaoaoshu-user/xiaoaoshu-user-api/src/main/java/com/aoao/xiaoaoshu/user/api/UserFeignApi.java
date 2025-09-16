package com.aoao.xiaoaoshu.user.api;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.constant.ApiConstants;
import com.aoao.xiaoaoshu.user.model.dto.RegisterUserReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author aoao
 * @create 2025-09-16-16:47
 */
@FeignClient(name = ApiConstants.SERVICE_NAME)
public interface UserFeignApi {
    String PREFIX = "/user";

    /**
     * 用户注册
     *
     * @param registerUserReqDTO
     * @return
     */
    @PostMapping(value = PREFIX + "/register")
    Result<Long> registerUser(@RequestBody RegisterUserReqDTO registerUserReqDTO);

}
