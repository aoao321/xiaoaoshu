package com.aoao.xiaoaoshu.auth.controller;

import com.alibaba.nacos.api.config.annotation.NacosProperty;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.apache.ibatis.annotations.Mapper;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author aoao
 * @create 2025-09-02-16:48
 */
@Controller
public class TestController {

    @NacosValue(value = "${rate-limit.api.limit}", autoRefreshed = true)
    private Integer limit;

    @RequestMapping ("/test")
    public String test() {
        return limit.toString();
    }

}
