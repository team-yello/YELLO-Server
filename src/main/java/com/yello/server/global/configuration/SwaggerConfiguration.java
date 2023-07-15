package com.yello.server.global.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(
    info = @Info(title = "YELL:O API Documentation",
        description = "YELL:O API 공식 명세서 입니다.",
        version = "v1",
        license = @License(
            name = "Apache License Version 2.0",
            url = "http://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    tags = {
        @Tag(name = "01. Vote", description = "투표(Yello) API"),
        @Tag(name = "02. Friend", description = "친구 API"),
        @Tag(name = "03. Authentication", description = "인증/인가 API"),
        @Tag(name = "04. User", description = "유저 API"),
    }
)
@RequiredArgsConstructor
@Configuration
public class SwaggerConfiguration {

}
