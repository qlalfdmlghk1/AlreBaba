package com.ssafy.alrebaba.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.ssafy.alrebaba.auth.application.CustomOAuth2UserService;
import com.ssafy.alrebaba.auth.application.RedisRefreshTokenService;
import com.ssafy.alrebaba.auth.jwt.CustomLogoutFilter;
import com.ssafy.alrebaba.auth.jwt.CustomSuccessHandler;
import com.ssafy.alrebaba.auth.jwt.JWTFilter;
import com.ssafy.alrebaba.auth.jwt.JWTUtil;
import com.ssafy.alrebaba.auth.jwt.LoginFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final RedisRefreshTokenService redisRefreshTokenService;

    @Value("${spring.jwt.access-token-name}") String accessTokenName;
    @Value("${spring.jwt.refresh-token-name}") String refreshTokenName;
    @Value("${spring.jwt.oauth-token-name}") String oauthTokenName;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CSRF 비활성화
        http.csrf((auth) -> auth.disable());

        // CORS 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));

        // Form 로그인 방식 비활성화
        http.formLogin((auth) -> auth.disable());

        // HTTP Basic 인증 방식 비활성화
        http.httpBasic((auth) -> auth.disable());

        // JWT 필터 추가
        http.addFilterBefore(new JWTFilter(jwtUtil, accessTokenName), LoginFilter.class);
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, redisRefreshTokenService, accessTokenName, refreshTokenName),
                UsernamePasswordAuthenticationFilter.class);

        // OAuth2 로그인 설정
        http.oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig.userService(customOAuth2UserService))
                .successHandler(customSuccessHandler)
        );

        // WebSocket 요청 허용 (STOMP 관련 경로 포함)
        http.authorizeHttpRequests((auth) -> auth
                // 기존 인증 정책 유지
                .requestMatchers("/", "/login/**", "/swagger-ui/**", "/swagger-resources/**",
                        "/v3/api-docs/**", "/logout", "/reissue", "/oauth2/**", "/health",
                        "/friends/**", "/studies/**", "/coding-tests/**", "/code/**","/ws-stomp/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/members/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/members/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/members/**").hasAnyRole("ADMIN", "AGENT", "USER")
                .requestMatchers(HttpMethod.DELETE, "/members/**").hasAnyRole("ADMIN", "AGENT", "USER")

                .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/images/**").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers(HttpMethod.PUT, "/images").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers(HttpMethod.DELETE, "/images").hasAnyRole("ADMIN", "AGENT")

                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        // 로그아웃 필터 추가
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, redisRefreshTokenService, refreshTokenName, oauthTokenName), LogoutFilter.class);

        // 로그아웃 설정
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "Authorization")
        );

        // 세션 설정 (STATELESS)
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // SecurityFilterChain에서 완전히 무시하도록 설정
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/ws-stomp/**", "/api/v1/ws-stomp/**")
                .requestMatchers("/chat/**");
    }
}
