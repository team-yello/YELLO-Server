package com.yello.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

    @Value("${server.port}")
    private static int port;

    public static void main(String[] args) {
        System.out.println(port);
        SpringApplication.run(ServerApplication.class, args);
    }

}


