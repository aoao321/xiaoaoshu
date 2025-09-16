package com.aoao.xiaoaoshu.user.model.dto.rsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: 犬小哈
 * @date: 2024/4/7 15:17
 * @version: v1.0.0
 * @description: 根据手机号查询用户信息
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserByPhoneRspDTO {

    private Long id;

    private String password;

    private String phone;

    private Integer status;

    private Boolean isDeleted;

}

