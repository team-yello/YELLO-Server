package com.yello.server.domain.authorization.exception;

import static com.yello.server.global.common.ErrorCode.AUTHENTICATION_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.global.common.dto.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

        ObjectMapper objectMapper = new ObjectMapper();
        log.error("인증에 실패했습니다.");

        CustomAuthenticationException exception =
            new CustomAuthenticationException(AUTHENTICATION_ERROR);
        response.setStatus(exception.getHttpStatus());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(
            response.getWriter(),
            BaseResponse.error(exception.getError())
        );
    }
}
