package com.flab.oasis.config;

import com.flab.oasis.filter.JwtFilter;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.service.OAuth2Service;
import com.flab.oasis.service.handler.OAuth2FailureHandler;
import com.flab.oasis.service.handler.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity(debug = false) // 스프링 시큐리티 필터를 스프링 필터체인에 등록
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwtService;
    private final OAuth2Service oAuth2Service;
    private final OAuth2SuccessHandler successHandler;
    private final OAuth2FailureHandler failureHandler;

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable() // csrf 인증 토큰 비활성
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 시큐리티 세션 비활성
                .and()
                .formLogin().disable() // 로그인 화면 비활성
                .httpBasic().disable() // Authorization 헤더의 Basic 타입 사용 안함
                .apply(new OasisAuthFilter())
                .and()
                .authorizeRequests(auth -> auth
                        .antMatchers("/api/auth/**").permitAll() // '/api/auth' 경로는 인증 안함
                        .anyRequest().authenticated() // 그 외 경로는 모두 인증 필요
                )
                .oauth2Login()
                .userInfoEndpoint()
                .userService(oAuth2Service)
                .and()
                .successHandler(successHandler)
                .failureHandler(failureHandler);

        return httpSecurity.build();
    }

    public class OasisAuthFilter extends AbstractHttpConfigurer<OasisAuthFilter, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);

            http.addFilter(corsFilter()) // cross-origin resource sharing 허가
                    .addFilter(new JwtFilter(authenticationManager, jwtService));
        }
    }

    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }
}
