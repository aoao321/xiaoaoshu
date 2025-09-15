package com.aoao.framework.biz.context.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 轻量级用户上下文，不依赖 auth
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {
    private Long userId;
    private List<String> roles;
    private List<String> permissions;
}
