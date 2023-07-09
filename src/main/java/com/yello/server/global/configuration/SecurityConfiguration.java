package com.yello.server.global.configuration;

import com.yello.server.domain.user.util.AuthTokenProvider;
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
        return http.csrf().disable()
                    .authorizeHttpRequests()
                    .antMatchers("/auth/oauth/**").permitAll()
                    .antMatchers("/auth/univ/**").permitAll()
                    .antMatchers("/auth/valid").permitAll()
                    .antMatchers("/*.html").permitAll()
                    .antMatchers("/").permitAll()
                    .antMatchers("/data").permitAll()
                .and()
                    .addFilterBefore(new AuthenticationFilter(authTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
