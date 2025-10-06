package com.aoao.xiaoaoshu.relation.biz.rpc;

import cn.hutool.core.collection.CollUtil;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.api.UserFeignApi;
import com.aoao.xiaoaoshu.user.model.dto.req.FindNoteCreatorByIdReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.req.FindNoteCreatorsByIdsReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindNoteCreatorByIdRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByIdRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author aoao
 * @create 2025-10-03-17:27
 */
@Service
public class UserRpcService {

    @Autowired
    private UserFeignApi userFeignApi;

    public FindNoteCreatorByIdRspDTO findNoteCreatorById(Long userId) {
        FindNoteCreatorByIdReqDTO findNoteCreatorByIdReqDTO = new FindNoteCreatorByIdReqDTO();
        findNoteCreatorByIdReqDTO.setId(userId);

        Result<FindNoteCreatorByIdRspDTO> response = userFeignApi.findNoteCreatorById(findNoteCreatorByIdReqDTO);

        if (Objects.isNull(response) || !response.isSuccess()) {
            return null;
        }

        return response.getData();
    }

    public List<FindNoteCreatorByIdRspDTO> findByIds(List<Long> userIds) {
        FindNoteCreatorsByIdsReqDTO findNoteCreatorsByIdsReqDTO = new FindNoteCreatorsByIdsReqDTO();
        findNoteCreatorsByIdsReqDTO.setIds(userIds);

        Result<List<FindNoteCreatorByIdRspDTO>> response = userFeignApi.findNoteCreatorsByIds(findNoteCreatorsByIdsReqDTO);

        if (!response.isSuccess() || Objects.isNull(response.getData()) || CollUtil.isEmpty(response.getData())) {
            return null;
        }

        return response.getData();
    }

}

