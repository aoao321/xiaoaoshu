package com.aoao.xiaoaoshu.kv.model.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author aoao
 * @create 2025-09-20-14:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindNoteContentReqDTO {
    @NotBlank(message = "笔记 ID 不能为空")
    private String uuid;
}
