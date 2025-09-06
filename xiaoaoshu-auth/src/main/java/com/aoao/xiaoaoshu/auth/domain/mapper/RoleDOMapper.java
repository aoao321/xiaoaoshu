package com.aoao.xiaoaoshu.auth.domain.mapper;

import com.aoao.xiaoaoshu.auth.domain.entity.RoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author aoao
 * @create 2025-08-25-20:19
 */
@Mapper
public interface RoleDOMapper {

    @Select("SELECT * FROM t_role WHERE id = #{roleId} AND is_deleted=0 AND status=0")
    RoleDO getById(Long roleId);

    List<String> findRoleByPhone(String phone);

    @Select("SELECT id FROM t_role WHERE status=0 AND is_deleted=0")
    List<Long> selectEnabledList();

}
