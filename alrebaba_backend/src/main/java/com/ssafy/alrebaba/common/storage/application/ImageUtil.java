package com.ssafy.alrebaba.common.storage.application;

import com.amazonaws.services.s3.AmazonS3;
import com.ssafy.alrebaba.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ImageUtil extends S3Service{
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");

    public ImageUtil(AmazonS3 amazonS3) {
        super(amazonS3);
    }

    public static boolean isImage(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return false;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            return false;
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        return IMAGE_EXTENSIONS.contains(extension);
    }

    @Override
    public String store(MultipartFile multipartFile, String s3FileName, String s3FolderName) throws IOException {
        if(!isImage(multipartFile)){
            throw new BadRequestException("이미지가 아닙니다.");
        }
        return super.store(multipartFile, s3FileName, s3FolderName);
    }

    @Override
    public String store(MultipartFile multipartFile) throws IOException {
        if(!isImage(multipartFile)){
            throw new BadRequestException("이미지가 아닙니다.");
        }
        return super.store(multipartFile);
    }

}
