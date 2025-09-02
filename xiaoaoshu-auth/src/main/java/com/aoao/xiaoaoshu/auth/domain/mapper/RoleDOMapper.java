package com.aoao.xiaoaoshu.auth.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author aoao
 * @create 2025-08-25-20:19
 */
@Mapper
public interface RoleDOMapper {
    List<String> findRoleByPhone(String phone);

    @Select("SELECT id FROM t_role WHERE status=0 AND is_deleted=0")
    List<Long> selectEnabledList();

}
