package com.yello.server.global;

import io.swagger.v3.oas.annotations.Hidden;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Hidden
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
