package com.aoao.xiaoaoshu.user.model.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aoao
 * @create 2025-09-17-0:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserByIdReqDTO {
    @NotNull
    private Long id;
}
