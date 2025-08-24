package com.aoao.xiaoaoshu.auth.domain.mapper;

import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserDO record);

    int insertSelective(UserDO record);

    UserDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);

    @Select("SELECT * FROM t_user WHERE phone = #{phone}")
    UserDO getByPhone(String phone);

    @Select("SELECT * FROM t_user WHERE id = #{id}")
    UserDO getById(Long id);
}