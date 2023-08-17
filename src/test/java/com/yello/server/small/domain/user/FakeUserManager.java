package com.yello.server.small.domain.user;

import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserManager;

public class FakeUserManager implements UserManager {

    private final static String OFFICIAL_NAME = "옐로팀";
    private final static String OFFICIAL_MALE_ID = "yello_male";
    private final static String OFFICIAL_FEMALE_ID = "yello_female";

    private final UserRepository userRepository;

    public FakeUserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getOfficialUser(Gender gender) {
        final String uuid = "F".equals(gender.intial()) ? OFFICIAL_MALE_ID : OFFICIAL_FEMALE_ID;

        return userRepository.findByUuid(uuid)
            .orElseGet(() ->
                userRepository.save(makeOfficialUser(OFFICIAL_NAME, uuid, gender))
            );
    }
    
    private User makeOfficialUser(String name, String yelloId, Gender gender) {
        return User.builder()
            .recommendCount(0L)
            .name(name)
            .yelloId(yelloId)
            .gender(gender)
            .point(0)
            .social(Social.KAKAO)
            .profileImage("")
            .uuid(yelloId)
            .deletedAt(null)
            .group(null)
            .groupAdmissionYear(0)
            .email("")
            .deviceToken(yelloId)
            .subscribe(Subscribe.NORMAL)
            .ticketCount(0)
            .build();
    }
}
