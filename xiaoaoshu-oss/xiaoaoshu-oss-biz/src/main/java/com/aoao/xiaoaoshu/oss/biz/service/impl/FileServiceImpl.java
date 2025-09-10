package com.aoao.xiaoaoshu.oss.biz.service.impl;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.oss.biz.service.FileService;
import com.aoao.xiaoaoshu.oss.biz.strategy.FileStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author aoao
 * @create 2025-09-09-16:18
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileStrategy fileStrategy;

    @Override
    public Result uploadFile(MultipartFile file) {
        String url = fileStrategy.uploadFile(file,"xiaoaoshu");

        return Result.success(url);
    }
}
