package com.aoao.xiaoaoshu.auth.domain.mapper;

import com.aoao.xiaoaoshu.auth.domain.entity.PermissionDO;
import com.aoao.xiaoaoshu.auth.model.dto.RolePermissionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author aoao
 * @create 2025-08-23-21:41
 */
@Mapper
public interface PermissionDOMapper {

    List<String> findPermissionByPhone(String phone);

    List<RolePermissionDTO> selectByRoleIds(@Param("ids") List<Long> roleIds);
}
