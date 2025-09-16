package com.aoao.xiaoaoshu.user.biz.domain.mapper;

import com.aoao.xiaoaoshu.user.biz.domain.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author aoao
 * @create 2025-09-11-14:53
 */
@Mapper
public interface UserDOMapper {
    @Update("UPDATE t_user SET avatar=#{user.avatar},xiaohashu_id=#{user.xiaoaoshuId},nickname=#{user.nickname},birthday=#{user.birthday},background_img=#{user.backgroundImg},sex=#{user.sex},introduction=#{user.introduction},update_time=#{user.updateTime} WHERE #{user.id} = id")
    void updateByPrimaryKeySelective(@Param("user") UserDO userDO);

    @Select("SELECT * FROM t_user WHERE phone = #{phone}")
    UserDO getByPhone(String phone);

    @Select("SELECT * FROM t_user WHERE id = #{id}")
    UserDO getById(Long id);

    void insert(@Param("user") UserDO userDO);

}
