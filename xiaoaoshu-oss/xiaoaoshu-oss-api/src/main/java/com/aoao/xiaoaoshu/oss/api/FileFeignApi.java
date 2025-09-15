package com.aoao.xiaoaoshu.oss.api;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.oss.constant.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author aoao
 * @create 2025-09-14-17:33
 */
@FeignClient(name = ApiConstants.SERVICE_NAME)
public interface FileFeignApi {

    String PREFIX = "/file";

    @PostMapping(value = PREFIX + "/test")
    Result<?> test();

}