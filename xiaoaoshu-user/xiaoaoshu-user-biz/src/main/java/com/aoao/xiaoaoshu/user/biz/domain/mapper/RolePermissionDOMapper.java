package com.aoao.xiaoaoshu.user.biz.domain.mapper;


import com.aoao.xiaoaoshu.user.biz.domain.entity.RolePermissionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author aoao
 * @create 2025-09-01-18:52
 */
@Mapper
public interface RolePermissionDOMapper {

    List<RolePermissionDO> selectByRoleIds(@Param("roleIds") List<Long> roleIds);
}
