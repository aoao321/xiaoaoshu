package com.aoao.xiaoaoshu.note.biz.rpc;

import com.aoao.xiaoaoshu.user.api.UserFeignApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author aoao
 * @create 2025-09-25-14:53
 */
@Service
public class UserRpcService {

    @Autowired
    private UserFeignApi userFeignApi;


}
