package com.aoao.xiaoaoshu.auth.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author aoao
 * @create 2025-08-25-20:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDO {
    /** 主键ID */
    private Long id;

    /** 角色名 */
    private String roleName;

    /** 角色唯一标识 */
    private String roleKey;

    /** 状态(0：启用 1：禁用) */
    private Integer status;

    /** 管理系统中的显示顺序 */
    private Integer sort;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 最后一次更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除(0：未删除 1：已删除) */
    private Boolean isDeleted;
}
