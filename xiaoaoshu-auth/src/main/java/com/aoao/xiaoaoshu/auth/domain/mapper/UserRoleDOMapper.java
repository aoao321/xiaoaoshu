package com.aoao.xiaoaoshu.auth.domain.mapper;

import com.aoao.xiaoaoshu.auth.domain.entity.UserRoleDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author aoao
 * @create 2025-08-25-20:29
 */
@Mapper
public interface UserRoleDOMapper {

    @Insert("INSERT INTO t_user_role_rel (user_id, role_id) VALUES (#{userRoleDO.userId},#{userRoleDO.roleId})")
    void insert(@Param("userRoleDO") UserRoleDO userRoleDO);
}
