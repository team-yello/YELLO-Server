package com.yello.server.domain.user.service;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    @Override
    public Boolean isYelloIdDuplicated(String yelloId) {
        if(Objects.isNull(yelloId))
            throw new CustomException(ErrorCode.NON_REQUIRED_REQUEST_DATA_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage());

        Optional<User> user = userRepository.findByYelloId(yelloId);

        if(user.isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage());

        return true;
    }
}
