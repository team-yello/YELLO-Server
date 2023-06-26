package com.yello.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.HashSet;
import java.util.Set;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Configuration
@EnableWebMvc
public class SwaggerConfiguration {
    private ApiInfo swaagerInformation() {
        return new ApiInfoBuilder()
                .title("YELL:O API Documentation")
                .description("YELL:O API 공식 명세서 입니다.")
                .build();
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(SWAGGER_2)
                .consumes(createConsumeContentTypes())
                .produces(createProduceContentTypes())
                .apiInfo(swaagerInformation())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.yello.server.controller"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false);
    }

    private Set<String> createConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json");
        return consumes;
    }

    private Set<String> createProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json");
        return produces;
    }


}
