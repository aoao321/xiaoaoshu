package com.aoao.xiaoaoshu.user.model.dto.req;

import com.aoao.framework.common.validator.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aoao
 * @create 2025-09-16-22:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindUserRoleByPhoneReqDTO {
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @PhoneNumber
    private String phone;
}
