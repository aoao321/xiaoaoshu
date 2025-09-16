package com.aoao.xiaoaoshu.user.biz.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author aoao
 * @create 2025-09-02-15:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionDTO {

    private Long roleId;

    private Long id;

    private Long parentId;

    private String name;

    private Integer type;

    private String menuUrl;

    private String menuIcon;

    private Integer sort;

    private String permissionKey;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean isDeleted;



}
