package com.aoao.xiaoaoshu.oss.biz.service;

import com.aoao.framework.common.result.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author aoao
 * @create 2025-09-09-16:16
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    Result uploadFile(MultipartFile file);
}
