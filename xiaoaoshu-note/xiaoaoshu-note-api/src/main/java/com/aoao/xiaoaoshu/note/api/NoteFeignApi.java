package com.aoao.xiaoaoshu.note.api;

import com.aoao.xiaoaoshu.note.constant.ApiConstant;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author aoao
 * @create 2025-09-23-0:28
 */
@FeignClient(name = ApiConstant.SERVICE_NAME)
public interface NoteFeignApi {
}
