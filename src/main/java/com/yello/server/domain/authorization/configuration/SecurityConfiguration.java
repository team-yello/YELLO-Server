package com.yello.server.domain.authorization.configuration;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.yello.server.domain.authorization.JwtTokenProvider;
import com.yello.server.domain.authorization.exception.CustomAuthenticationEntryPoint;
import com.yello.server.domain.authorization.filter.JwtExceptionFilter;
import com.yello.server.domain.authorization.filter.JwtFilter;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.global.exception.ExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final UserRepository userRepository;
    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .httpBasic().disable()
            .csrf().disable()
            .cors().and()
            .authorizeRequests()
            .antMatchers("/api/v1/auth/oauth").permitAll()
            .antMatchers("/api/v1/auth/signup").permitAll()
            .antMatchers(GET, "/api/*").authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(STATELESS) // jwt 사용하는 경우 사용
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .and()
            .addFilterBefore(new ExceptionHandlerFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtFilter(new JwtTokenProvider(), secretKey, userRepository),
                UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtExceptionFilter(), JwtFilter.class)
            .build();
    }
}
