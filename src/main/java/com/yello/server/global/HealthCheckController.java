package com.yello.server.global;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    @GetMapping("/")
    public String healthCheck() {
        return "Yell:o world!";
    }
}
