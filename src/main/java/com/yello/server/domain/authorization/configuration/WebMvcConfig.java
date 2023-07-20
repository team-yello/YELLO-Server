package com.yello.server.domain.authorization.configuration;

import com.yello.server.global.common.annotation.AccessTokenUserResolver;
import com.yello.server.global.common.annotation.ServiceTokenResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AccessTokenUserResolver accessTokenUserResolver;
    private final ServiceTokenResolver serviceTokenResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(accessTokenUserResolver);
        resolvers.add(serviceTokenResolver);
    }
}
