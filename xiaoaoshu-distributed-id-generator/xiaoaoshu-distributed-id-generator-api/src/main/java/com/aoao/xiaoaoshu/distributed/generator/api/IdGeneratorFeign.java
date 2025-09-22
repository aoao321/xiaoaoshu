package com.aoao.xiaoaoshu.distributed.generator.api;

import com.aoao.xiaoaoshu.distributed.generator.constant.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author aoao
 * @create 2025-09-21-23:02
 */
@FeignClient(name = ApiConstants.SERVICE_NAME)
public interface IdGeneratorFeign {
    String PREFIX = "/id";

    @GetMapping(value = PREFIX + "/segment/get/{key}")
    String getSegmentId(@PathVariable("key") String key);

    @GetMapping(value = PREFIX + "/snowflake/get/{key}")
    String getSnowflakeId(@PathVariable("key") String key);
}
