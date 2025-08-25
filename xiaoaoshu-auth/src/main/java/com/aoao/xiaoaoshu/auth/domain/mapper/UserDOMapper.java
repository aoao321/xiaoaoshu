package com.aoao.xiaoaoshu.auth.domain.mapper;

import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDOMapper {


    @Select("SELECT * FROM t_user WHERE phone = #{phone}")
    UserDO getByPhone(String phone);

    @Select("SELECT * FROM t_user WHERE id = #{id}")
    UserDO getById(Long id);

    void insert(@Param("userDO") UserDO userDO);
}