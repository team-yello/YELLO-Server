package com.yello.server.global.configuration;

import com.yello.server.domain.auth.util.AuthTokenProvider;
import com.yello.server.global.security.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthTokenProvider authTokenProvider;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("http = " + http);
        return http.csrf().disable()
                    .authorizeHttpRequests()
                    .anyRequest().permitAll()
//                    .antMatchers("/api/v1/auth/oauth/**").permitAll()
//                    .antMatchers("/api/v1/auth/univ/**").permitAll()
//                    .antMatchers("/api/v1/auth/valid").permitAll()
//                    .antMatchers("/*.html").permitAll()
//                    .antMatchers("/data").permitAll()
//                    .antMatchers("/swagger-ui/**").permitAll()
//                    .antMatchers("/").permitAll()
                .and()
                    .addFilterBefore(new AuthenticationFilter(authTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
