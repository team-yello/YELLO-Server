package com.yello.server.global.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(
        info = @Info(title = "YELL:O API Documentation",
                description = "YELL:O API 공식 명세서 입니다.",
                version = "v1"))
@RequiredArgsConstructor
@Configuration
public class SwaggerConfiguration {
    @Bean
    public GroupedOpenApi openApi() {
        String[] paths = {"api/v1/**"};

        return GroupedOpenApi.builder()
                .group("V1 API")
                .pathsToMatch(paths)
                .build();
    }
}
