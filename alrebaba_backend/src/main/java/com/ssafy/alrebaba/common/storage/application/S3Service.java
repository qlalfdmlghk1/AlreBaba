package com.ssafy.alrebaba.common.storage.application;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service implements StorageService{
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;


    /* 1. 파일 업로드 */
    public String store(MultipartFile multipartFile, String s3FileName, String s3FolderName) throws IOException {
        // 메타데이터 생성
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(multipartFile.getInputStream().available());
        // putObject(버킷명, 파일명, 파일데이터, 메타데이터)로 S3에 객체 등록
        String UUIDName = s3FolderName +"/"+ makeUUIDName(s3FileName);
        amazonS3.putObject(bucket, UUIDName, multipartFile.getInputStream(), objMeta);
        // 등록된 객체의 url 반환 (decoder: url 안의 한글or특수문자 깨짐 방지)
        System.out.println(UUIDName);
//        return URLDecoder.decode(amazonS3.getUrl(bucket, UUIDName).toString(), "utf-8");
        return UUIDName;
    }

    public String store(MultipartFile multipartFile) throws  IOException{
        return this.store(multipartFile, multipartFile.getName(), "");
    }

    public String store(MultipartFile multipartFile, String s3FolderName) throws  IOException{
        return this.store(multipartFile, multipartFile.getOriginalFilename(),s3FolderName);
    }

    /* 2. 파일 삭제 */
    public void deleteOne (String keyName) {
        try {
            // deleteObject(버킷명, 키값)으로 객체 삭제
            amazonS3.deleteObject(bucket, keyName);
        } catch (AmazonServiceException e) {
            log.error(e.toString());
        }
    }

    /* 3. 파일의 presigned URL 반환 */
    public String getPreSignedUrl (String keyName) {
        String preSignedURL = "";
        // presigned URL이 유효하게 동작할 만료기한 설정 (2분)
        Date expiration = new Date();
        Long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);

        try {
            // presigned URL 발급
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, keyName)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
            preSignedURL = url.toString();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return preSignedURL;
    }

    public String makeUUIDName(MultipartFile multipartFile){
        return UUID.randomUUID() +multipartFile.getOriginalFilename();
    }

    public String makeUUIDName(String string){
        return UUID.randomUUID() +string;
    }


//    public String submitOneImageFile (MultipartFile multipartFile, String imageName ) throws IOException {
//        upload(multipartFile, imageName);
//        return imageName;
//    }
//
//    public boolean deleteOneImageFile(String keyName){
//        try{
//            amazonS3.deleteObject(bucket, keyName);
//        }catch ()
//    }



}