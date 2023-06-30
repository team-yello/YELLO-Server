package com.yello.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/")
    public String healthCheck() {
        return "Yell:o world!";
    }

    /**
     * Redis health check
     */
    @PostMapping("/data")
    public ResponseEntity<String> setRedisData(@RequestBody(required = true) Map<String, String> map) throws Exception {
        redisTemplate.opsForValue().set(map.get("key"), map.get("value"));
        return new ResponseEntity<>("정상 등록", HttpStatus.CREATED);
    }

    @GetMapping("/data")
    public ResponseEntity<String> getRedisData(@RequestParam(required = true) String key) {
        return new ResponseEntity<>(redisTemplate.opsForValue().get(key), HttpStatus.OK);
    }
}
