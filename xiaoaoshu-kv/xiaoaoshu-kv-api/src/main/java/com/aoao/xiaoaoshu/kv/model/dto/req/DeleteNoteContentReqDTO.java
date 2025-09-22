package com.aoao.xiaoaoshu.kv.model.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aoao
 * @create 2025-09-20-14:12
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteNoteContentReqDTO {

    @NotBlank(message = "笔记 ID 不能为空")
    private String uuid;

}
