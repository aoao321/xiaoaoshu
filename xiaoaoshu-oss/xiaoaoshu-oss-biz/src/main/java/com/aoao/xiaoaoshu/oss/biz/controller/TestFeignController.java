package com.aoao.xiaoaoshu.oss.biz.controller;

import com.aoao.framework.biz.operationlog.annotation.Log;
import com.aoao.framework.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aoao
 * @create 2025-09-14-17:26
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class TestFeignController {

    @PostMapping(value = "/test")
    @Log("Feign 测试接口")
    public Result test() {
        return Result.success();
    }

}
