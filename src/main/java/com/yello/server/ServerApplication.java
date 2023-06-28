package com.yello.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class ServerApplication {

    @Value("${server.port}")
    private int port;

    public static void main(String[] args) {
//        SpringApplication.run(ServerApplication.class, args);
        ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);

        ServerApplication serverApplication = context.getBean(ServerApplication.class);
        serverApplication.printPort();
    }

    private void printPort() {
        System.out.println("Server port: " + port);
    }

}