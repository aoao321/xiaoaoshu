package com.aoao.xiaoaoshu.user.biz.rpc;

import com.aoao.xiaoaoshu.distributed.generator.api.IdGeneratorFeignApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author aoao
 * @create 2025-09-21-23:22
 */
@Component
public class IdGeneratorRpcService {

    @Autowired
    private IdGeneratorFeignApi idGeneratorFeignApi;

    /**
     * Leaf 号段模式：小哈书 ID 业务标识
     */
    private static final String BIZ_TAG_XIAOAOSHU_ID = "leaf-segment-xiaoaoshu-id";

    public String generateXiaoaoshuId(){
        return idGeneratorFeignApi.getSegmentId(BIZ_TAG_XIAOAOSHU_ID);
    }

    /**
     * Leaf 号段模式：用户 ID 业务标识
     */
    private static final String BIZ_TAG_USER_ID = "leaf-segment-user-id";

    public String generateUserId(){
        return idGeneratorFeignApi.getSegmentId(BIZ_TAG_USER_ID);
    }


}
