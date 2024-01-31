package com.yello.server.domain.user;

import static com.yello.server.global.common.ErrorCode.USER_DATA_NOT_FOUND_EXCEPTION;

import com.yello.server.domain.user.entity.UserData;
import com.yello.server.domain.user.entity.UserDataType;
import com.yello.server.domain.user.exception.UserDataNotFoundException;
import com.yello.server.domain.user.repository.UserDataRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeUserDataRepository implements UserDataRepository {

    private final List<UserData> data = new ArrayList<>();
    private Long id = 0L;

    @Override
    public UserData save(UserData userData) {
        if (userData.getId() != null && userData.getId() > id) {
            id = userData.getId();
        }

        UserData newUserData = UserData.builder()
            .id(userData.getId() == null ? ++id : userData.getId())
            .user(userData.getUser())
            .tag(userData.getTag())
            .value(userData.getValue())
            .build();

        data.add(newUserData);
        return newUserData;
    }

    @Override
    public void update(Long userId, UserDataType tag, String value) {
        for (int i = 0; i < data.size(); i++) {
            final UserData userData = data.get(i);
            if (userData.getUser().getId().equals(userId) && userData.getTag().equals(tag)) {
                data.set(i, UserData.of(userData.getTag(), value, userData.getUser()));
            }
        }
    }

    @Override
    public UserData getByUserIdAndTag(Long userId, UserDataType tag) {
        return data.stream()
            .filter(userData -> userData.getUser().getId().equals(userId)
                && userData.getTag().equals(tag))
            .findFirst()
            .orElseThrow(() -> new UserDataNotFoundException(USER_DATA_NOT_FOUND_EXCEPTION));
    }

    @Override
    public Optional<UserData> findByUserIdAndTag(Long userId, UserDataType tag) {
        return data.stream()
            .filter(userData -> userData.getUser().getId().equals(userId)
                && userData.getTag().equals(tag))
            .findFirst();
    }
}
