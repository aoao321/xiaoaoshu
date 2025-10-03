package com.aoao.xiaoaoshu.relation.biz.rpc;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.api.UserFeignApi;
import com.aoao.xiaoaoshu.user.model.dto.req.FindNoteCreatorByIdReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindNoteCreatorByIdRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}

