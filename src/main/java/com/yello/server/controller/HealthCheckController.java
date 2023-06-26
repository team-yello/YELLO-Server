package com.yello.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
public class HealthCheckController {
    @GetMapping("/")
    public String healthCheck() {
        return "Yell:o world";
    }
}
