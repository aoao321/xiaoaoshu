package com.aoao.xiaoaoshu.user.biz.rpc;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.oss.api.FileFeignApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author aoao
 * @create 2025-09-16-14:48
 */
@Component
public class OssRpcService {

    @Autowired
    private FileFeignApi fileFeignApi;

    public String uploadFile(MultipartFile file) {
        // 调用对象存储服务上传文件
        Result<?> response = fileFeignApi.upload(file);

        if (!response.isSuccess()) {
            return null;
        }
        // 返回图片访问链接
        return (String) response.getData();
    }
}