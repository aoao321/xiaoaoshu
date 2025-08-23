package com.aoao.xiaoaoshu.auth.domain.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author aoao
 * @create 2025-08-23-21:41
 */
@Mapper
public interface PermissionDOMapper {

    List<String> findPermissonByPhone(String phone);
}
