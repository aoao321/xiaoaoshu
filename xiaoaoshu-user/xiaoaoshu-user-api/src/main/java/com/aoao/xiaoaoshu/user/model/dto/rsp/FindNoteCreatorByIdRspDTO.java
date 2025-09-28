package com.aoao.xiaoaoshu.user.model.dto.rsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aoao
 * @create 2025-09-25-14:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindNoteCreatorByIdRspDTO {
    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;
}
