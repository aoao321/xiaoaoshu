package com.aoao.xiaoaoshu.auth.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 小奥数ID
     */
    private String xiaohashuId;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 背景图
     */
    private String backgroundImg;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别 (0:未知, 1:男, 2:女)
     */
    private Integer sex;

    /**
     * 状态 (0:正常, 1:禁用, 2:冻结等)
     */
    private Integer status;

    /**
     * 个人介绍
     */
    private String introduction;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除 (0:未删除, 1:已删除)
     */
    private Boolean isDeleted;


}