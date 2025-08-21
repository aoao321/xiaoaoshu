package com.aoao.xiaoaoshu.auth.controller;

import com.aoao.framework.biz.operationlog.annotation.Log;
import com.aoao.framework.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @author aoao
 * @create 2025-08-18-21:36
 */
@RestController
public class TestController {

    @PostMapping("/test")
    @Log(value = "你好")
    public Result test(@RequestBody LocalDateTime time) {
        return Result.success(time);
    }
}
