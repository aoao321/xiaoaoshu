package com.aoao.xiaoaoshu.oss.biz.strategy.impl;

import com.aoao.xiaoaoshu.oss.biz.properties.MinioProperties;
import com.aoao.xiaoaoshu.oss.biz.strategy.FileStrategy;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @author aoao
 * @create 2025-09-09-16:11
 */
@Slf4j
public class MinioFileStrategy implements FileStrategy {

    @Resource
    private MinioClient minioClient;
    @Resource
    private MinioProperties minioProperties;

    @Override
    @SneakyThrows
    public String uploadFile(MultipartFile file, String bucketName) {
        if (file.isEmpty()) {
            return null;
        }
        // 获取文件名称
        final String originalFilename = file.getOriginalFilename();
        // 文件的 Content-Type
        String contentType = file.getContentType();
        // 获取后缀
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成存储对象的名称（将 UUID 字符串中的 - 替换成空字符串）
        String key = UUID.randomUUID().toString().replace("-", "");
        // 拼接文件名
        String objectName = String.format("%s%s", key, ext);
        // 上传文件
        // 上传文件至 Minio
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(contentType)
                .build());
        // 返回文件的访问链接
        String url = String.format("%s/%s/%s", minioProperties.getEndpoint(), bucketName, objectName);
        return url;
    }
}
