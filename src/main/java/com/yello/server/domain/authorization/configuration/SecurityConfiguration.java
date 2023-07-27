package com.yello.server.domain.authorization.configuration;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.yello.server.domain.authorization.JwtTokenProvider;
import com.yello.server.domain.authorization.exception.CustomAuthenticationEntryPoint;
import com.yello.server.domain.authorization.filter.JwtExceptionFilter;
import com.yello.server.domain.authorization.filter.JwtFilter;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.global.exception.ExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
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
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .httpBasic().disable()
            .csrf().disable()
            .cors()
            .and()
            .authorizeRequests()
            .antMatchers("/api/v1/auth/oauth", "/api/v1/auth/signup").permitAll()
            .antMatchers("/api/*").authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .and()
            .addFilterBefore(new JwtFilter(userRepository), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtExceptionFilter(jwtTokenProvider), JwtFilter.class)
            .addFilterBefore(new ExceptionHandlerFilter(), JwtExceptionFilter.class)
            .build();
    }
}
