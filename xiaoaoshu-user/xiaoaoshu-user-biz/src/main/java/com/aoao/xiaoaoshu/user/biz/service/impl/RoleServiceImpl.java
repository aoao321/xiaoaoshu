package com.aoao.xiaoaoshu.user.biz.service.impl;

import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.biz.domain.entity.UserDO;
import com.aoao.xiaoaoshu.user.biz.domain.mapper.RoleDOMapper;
import com.aoao.xiaoaoshu.user.biz.domain.mapper.UserDOMapper;
import com.aoao.xiaoaoshu.user.biz.service.RoleService;
import com.aoao.xiaoaoshu.user.model.dto.req.FindUserRoleByPhoneReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserRoleByPhoneRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author aoao
 * @create 2025-09-16-23:02
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDOMapper roleDOMapper;
    @Autowired
    private UserDOMapper userDOMapper;

    @Override
    public Result<List<FindUserRoleByPhoneRspDTO>> findRoleByPhone(FindUserRoleByPhoneReqDTO findUserRoleByPhoneReqDTO) {
        String phone = findUserRoleByPhoneReqDTO.getPhone();
        UserDO userDO = userDOMapper.getByPhone(phone);
        if (Objects.isNull(userDO)) {
            return Result.fail(ResponseCodeEnum.ABSENT_USER);
        }
        // 获取权限集合
        List<String> roleList = roleDOMapper.findRoleByPhone(phone);
        if (roleList == null || roleList.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        List<FindUserRoleByPhoneRspDTO> findUserRoleByPhoneRspDTOS = new ArrayList<>();
        roleList.forEach(role -> {
            FindUserRoleByPhoneRspDTO dto = new FindUserRoleByPhoneRspDTO();
            dto.setRoleKey(role); // 明确赋值
            findUserRoleByPhoneRspDTOS.add(dto);
        });

        return Result.success(findUserRoleByPhoneRspDTOS);
    }
}
