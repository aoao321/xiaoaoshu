package com.aoao.xiaoaoshu.note.biz.rpc;

import com.aoao.xiaoaoshu.distributed.generator.api.IdGeneratorFeignApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author aoao
 * @create 2025-09-23-18:23
 */
@Service
public class IdGeneratorRpcService {
    @Autowired
    private IdGeneratorFeignApi idGeneratorFeignApi;

    public String generateNoteId() {
        return idGeneratorFeignApi.getSnowflakeId("test");
    }
}
