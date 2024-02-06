package com.yello.server.global.common.dto;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Map;
import org.apache.tomcat.util.http.fileupload.IOUtils;

public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {

    private final Enumeration<String> headerNames;
    private final Map<String, String> headers;
    private ByteArrayOutputStream cachedBytes;

    public MultiReadHttpServletRequest(HttpServletRequest request) {
        super(request);

        headerNames = request.getHeaderNames();
        headers = new java.util.HashMap<>();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (cachedBytes == null) {
            cacheInputStream();
        }

        return new CachedServletInputStream(cachedBytes.toByteArray());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    private void cacheInputStream() throws IOException {
        /* Cache the inputstream in order to read it multiple times. For
         * convenience, I use apache.commons IOUtils
         */
        cachedBytes = new ByteArrayOutputStream();
        IOUtils.copy(super.getInputStream(), cachedBytes);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        headers.keySet()
            .forEach((key) -> {
                builder
                    .append(key)
                    .append(": ")
                    .append(headers.get(key))
                    .append("\n");
            });

        return builder.toString();
    }

    /* An input stream which reads the cached request body */
    private static class CachedServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream buffer;

        public CachedServletInputStream(byte[] contents) {
            this.buffer = new ByteArrayInputStream(contents);
        }

        @Override
        public int read() {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new RuntimeException("Not implemented");
        }
    }
}