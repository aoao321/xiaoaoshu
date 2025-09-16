package com.aoao.xiaoaoshu.oss.api;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.oss.config.FeignFormConfig;
import com.aoao.xiaoaoshu.oss.constant.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author aoao
 * @create 2025-09-14-17:33
 */
@FeignClient(name = ApiConstants.SERVICE_NAME,configuration = FeignFormConfig.class)
public interface FileFeignApi {

    String PREFIX = "/file";

    @PostMapping(value = PREFIX + "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<?> upload(@RequestPart(value = "file") MultipartFile file);

}