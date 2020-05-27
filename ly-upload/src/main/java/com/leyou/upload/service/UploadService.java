package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class UploadService {

    Logger logger = LoggerFactory.getLogger(UploadService.class);

    //文件的上传对象
    @Autowired
    private FastFileStorageClient storageClient;

    public String uploadImage(MultipartFile file) {
        String url = null;
        try {
            // 图片的名称
            String originalFilename = file.getOriginalFilename();//asdfdsf.jpg
            // 获取文件后缀名
            String ext = StringUtils.substringAfterLast(originalFilename,".");
            // 上传
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
            // 返回完整路径
            url = "http://image.leyou.com/" + storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }
}
