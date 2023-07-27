package com.yello.server.global.exception;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.common.dto.BaseResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException exception) {
            setErrorResponse(response, exception.getError());
        }
    }

    private void setErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(errorCode.getHttpStatusCode());
        response.setContentType(APPLICATION_JSON_VALUE);
        BaseResponse baseResponse = BaseResponse.error(errorCode);
        try {
            response.getWriter()
                    .write(objectMapper.configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true)
                            .writeValueAsString(baseResponse));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
