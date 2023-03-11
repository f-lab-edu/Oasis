package com.flab.oasis.config;

import com.flab.oasis.filter.JwtFilter;
import com.flab.oasis.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity // 스프링 시큐리티 필터를 스프링 필터체인에 등록
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf().disable() // csrf 인증 토큰 비활성
                .headers().frameOptions().disable()
                .and()
                .cors().configurationSource(corsConfigurationSource()) // cross-origin resource sharing 허가
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 시큐리티 세션 비활성
                .and()
                .formLogin().disable() // 로그인 화면 비활성
                .httpBasic().disable() // Authorization 헤더의 Basic 타입 사용 안함
                .authorizeRequests()
                .antMatchers("/api/auth").permitAll() // 인증 없이 접근 허용
                .anyRequest().authenticated() // 허용된 uri 외에 모두 인증
                .and()
                .addFilterBefore(new JwtFilter(jwtService), UsernamePasswordAuthenticationFilter.class) // UsernamePasswordAuthenticationFilter 이전에 jwtFilter 실행
                .build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
