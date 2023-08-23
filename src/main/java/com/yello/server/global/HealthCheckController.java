package com.yello.server.global;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    @GetMapping("/")
    public String healthCheck() {
        return "Yell:o world!";
    }
}
