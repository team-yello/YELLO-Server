package com.yello.server.global.security;

import com.yello.server.domain.auth.dto.ServiceTokenVO;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class AccessUserService implements UserDetailsService {

    private final ValueOperations<Long, ServiceTokenVO> tokenValueOperations;

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User account = userRepository.findAllByName(username)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER_EXCEPTION, NOT_FOUND_USER_EXCEPTION.getMessage()));

        if (null != account) {
            return new AppUser(account);
        } else {
            throw new UsernameNotFoundException(
                    String.format("can't find an account with the given username [%s]", username));
        }
    }

    public UserDetails loadByAccountId(long accountId) throws UsernameNotFoundException {
        User account = userRepository.findById(accountId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER_EXCEPTION, NOT_FOUND_USER_EXCEPTION.getMessage()));

        if (null == account) {
            throw new UsernameNotFoundException(
                    String.format("can't find an account with the given ID [%s]", accountId));
        }

        return new AppUser(account);
    }

    public boolean updateTokens(String uuid, String oldRefreshToken, String accessToken, String refreshToken) {
        User account = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER_EXCEPTION, NOT_FOUND_USER_EXCEPTION.getMessage()));

        ServiceTokenVO tokens = tokenValueOperations.get(account.getId());

        try {
            tokens.refreshToken();

            tokenValueOperations.set(
                    account.getId(),
                    ServiceTokenVO.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build()
            );
        } catch (NullPointerException e) {
            System.out.println("e = " + e);
            System.out.println("해당 유저" + account.getName() + "의 refreshToken이 존재하지 않습니다");
            return false;
        }

        return true;
    }

}
