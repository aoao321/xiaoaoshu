package com.aoao.xiaoaoshu.relation.biz.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowUnfollowUserMqDTO implements Serializable {

    private String key;

    private Long userId;

    private Long followUserId;

    private LocalDateTime createTime;
}

