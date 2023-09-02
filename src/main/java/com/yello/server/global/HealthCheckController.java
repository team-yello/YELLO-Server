package com.yello.server.global;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    @GetMapping("/")
    public String healthCheck() {
        return "Yell:o world!";
    }

    @PostMapping("/")
    public String amazon(@RequestBody String string) {
        return string;
    }

    @GetMapping("/abc")
    public void text() throws Exception {
        throw new NullPointerException();
    }
}
