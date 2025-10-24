package com.ssafy.alrebaba.common.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@Primary
public class CorsConfig implements CorsConfigurationSource {

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 오리진 설정 (클라이언트 도메인으로 변경하세요)
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173", "http://localhost:8080", "https://i12a702.p.ssafy.io", "https://i12a702.p.ssafy.io:443","https://i12a702.p.ssafy.io:80","https://i12a702.p.ssafy.io:8443","https://192.168.45.38:5173","https://219.254.47.60:5173" ,"https://192.168.219.166:5173", "https://112.147.244.206:5173", "https://70.12.247.117:5173","http://localhost:80","http://localhost:443", "http://70.12.247.117:5173",
                "https://70.12.247.117:5173", "https://70.12.247.114:5173" ,"https://70.12.247.118:5173",
                "https://192.168.219.166:3000","https://192.168.199.182:3000",
                "https://192.168.45.76:5173", "https://192.168.45.125:5173", "https://192.168.45.205:5173", "https://219.254.47.60:5173", "https://1.237.24.168:5173",
                "https://192.168.35.85:5173","https://1.229.185.170:5173", "https://192.168.0.32:5173",
                "https://112.147.244.206:5173", "https://192.168.219.166:5173", "https://192.168.45.98:5173", "http://70.12.247.118:5173","http://70.12.247.117:5173","http://70.12.247.114:5173" , "https://i12a702.p.ssafy.io", "https://172.30.1.20:5173", "https://121.138.56.205:5173","https://192.168.0.33:5173",
                "https://70.12.247.116:5173","https://70.12.115.45:5173",
                "https://70.12.247.115:5173", "https://70.12.247.116:3000","https://70.12.247.113:5173", "https://192.168.1.149:5173"

        ));


        // 허용할 HTTP 메서드 설정
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 허용할 헤더 설정
        config.setAllowedHeaders(Arrays.asList("*"));

        // 노출할 헤더 설정 (클라이언트에서 접근 가능한 헤더)
        config.setExposedHeaders(Arrays.asList("access"));

        // 인증 정보 허용 여부 설정
        config.setAllowCredentials(true);

        return config;
    }
}
