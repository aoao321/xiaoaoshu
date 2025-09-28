package com.aoao.xiaoaoshu.user.model.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aoao
 * @create 2025-09-25-14:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindNoteCreatorByIdReqDTO {
    @NotNull(message = "用户id不能为空")
    private Long id;
}
