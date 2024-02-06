package com.yello.server.global.exception;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.global.common.dto.MultiReadHttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest(request);

        try {
            filterChain.doFilter(multiReadRequest, response);
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
