package com.yello.server.global.common.annotation;

import static com.yello.server.domain.authorization.filter.JwtFilter.BEARER;
import static com.yello.server.global.common.ErrorCode.AUTHENTICATION_ERROR;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.exception.CustomAuthenticationException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class ServiceTokenResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasServiceToken = parameter.hasParameterAnnotation(ServiceToken.class);
        boolean isServiceTokenVOType =
            ServiceTokenVO.class.isAssignableFrom(parameter.getParameterType());
        return hasServiceToken && isServiceTokenVOType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String accessTokenHeader = webRequest.getHeader("X-ACCESS-AUTH");
        String refreshTokenHeader = webRequest.getHeader("X-REFRESH-AUTH");

        if (accessTokenHeader == null || refreshTokenHeader == null
            || !refreshTokenHeader.startsWith(BEARER)
            || !accessTokenHeader.startsWith(BEARER)) {
            throw new CustomAuthenticationException(AUTHENTICATION_ERROR);
        }

        String accessToken = accessTokenHeader.substring(BEARER.length());
        String refreshToken = refreshTokenHeader.substring(BEARER.length());
        return ServiceTokenVO.of(accessToken, refreshToken);
    }
}
