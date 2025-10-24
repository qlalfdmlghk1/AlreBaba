package com.ssafy.alrebaba.common.storage.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {

    String store(MultipartFile file, String s3FileName, String s3FolderName) throws IOException;
    String getPreSignedUrl(String filename);

    //    void deleteAll();
    void deleteOne(String fileName);
    String makeUUIDName(MultipartFile multipartFile);
    String makeUUIDName(String fileName);
}