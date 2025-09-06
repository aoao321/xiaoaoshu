package com.aoao.xiaoaoshu.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aoao
 * @create 2025-09-06-14:19
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hellow")
    public String hellow() {
        return "hellow";
    }
}
