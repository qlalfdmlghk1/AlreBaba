//package com.ssafy.alrebaba.common.storage.domain;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@ConfigurationProperties("storage")
//@Component
//public class StorageProperties {
//    private String location;
//
//    public StorageProperties(@Value("${file.path.upload-files}") String location) {
//        this.location = location;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//}