package com.yello.server.domain.authorization.configuration;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.yello.server.domain.authorization.exception.CustomAuthenticationEntryPoint;
import com.yello.server.domain.authorization.filter.JwtExceptionFilter;
import com.yello.server.domain.authorization.filter.JwtFilter;
import com.yello.server.domain.authorization.service.TokenProvider;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.global.exception.ExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .httpBasic(httpSecurityHttpBasicConfigurer -> {
                httpSecurityHttpBasicConfigurer.disable();
            })
            .csrf(httpSecurityCsrfConfigurer -> {
                httpSecurityCsrfConfigurer.disable();
            })
            .cors(httpSecurityCorsConfigurer -> {
                httpSecurityCorsConfigurer.disable();
            })
            .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                authorizationManagerRequestMatcherRegistry
                    .anyRequest().permitAll();
            })
            .sessionManagement(httpSecuritySessionManagementConfigurer -> {
                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(STATELESS);
            })
            .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(customAuthenticationEntryPoint);
            })
            .addFilterBefore(new JwtFilter(userRepository), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtExceptionFilter(tokenProvider), JwtFilter.class)
            .addFilterBefore(new ExceptionHandlerFilter(), JwtExceptionFilter.class)
            .build();
    }
}
