package com.aoao.xiaoaoshu.auth.domain.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author aoao
 * @create 2025-08-25-20:19
 */
@Mapper
public interface RoleDOMapper {
    List<String> findRoleByPhone(String phone);
}
