package com.aoao.xiaoaoshu.kv.api;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.kv.constant.ApiConstants;
import com.aoao.xiaoaoshu.kv.model.dto.req.AddNoteContentReqDTO;
import com.aoao.xiaoaoshu.kv.model.dto.req.DeleteNoteContentReqDTO;
import com.aoao.xiaoaoshu.kv.model.dto.req.FindNoteContentReqDTO;
import com.aoao.xiaoaoshu.kv.model.dto.rsp.FindNoteContentRspDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * @author aoao
 * @create 2025-09-20-13:41
 */
@FeignClient(name = ApiConstants.SERVICE_NAME)
public interface KVFeignApi {

    String PREFIX = "/kv";

    @PostMapping(value = PREFIX + "/note/content/add")
    Result addNoteContent(@RequestBody AddNoteContentReqDTO addNoteContentReqDTO);

    @PostMapping(value = PREFIX + "/note/content/find")
    Result<FindNoteContentRspDTO> findNoteContent(@RequestBody FindNoteContentReqDTO findNoteContentReqDTO);

    @PostMapping(value = PREFIX + "/note/content/delete")
    Result deleteNoteContent(@RequestBody DeleteNoteContentReqDTO deleteNoteContentReqDTO);
}
