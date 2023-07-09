package com.yello.server.global;

import io.swagger.v3.oas.annotations.Hidden;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @Controller
    public static class ThymeleafController {

        @Value("${prod-kakao-web-key}")
        private String kakaoWebKey;
//        @Value("${dev-url}")
        @Value("${prod-url}")
        private String kakaoRedirectUrl;

        @RequestMapping("/kakao.html")
        public String kakaoView(Model model) {
            model.addAttribute("kakaoWebKey", kakaoWebKey);
            model.addAttribute("kakaoRedirectUrl", kakaoRedirectUrl);
            return "kakao.html";
        }
    }
}
