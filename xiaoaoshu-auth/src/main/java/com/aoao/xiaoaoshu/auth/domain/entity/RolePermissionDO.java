package com.aoao.xiaoaoshu.auth.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author aoao
 * @create 2025-09-02-14:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolePermissionDO {

    private Long id;

    private Long roleId;

    private Long permissionId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean isDeleted;

}
