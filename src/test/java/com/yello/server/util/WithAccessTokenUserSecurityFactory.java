package com.yello.server.util;

import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAccessTokenUserSecurityFactory implements WithSecurityContextFactory<WithAccessTokenUser> {

    @Override
    public SecurityContext createSecurityContext(WithAccessTokenUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserGroup userGroup = UserGroup.builder()
            .groupName("school")
            .subGroupName("department")
            .build();

        User user = User.builder()
            .id(1L)
            .recommendCount(0L)
            .name("name")
            .yelloId("yelloId")
            .gender(Gender.MALE)
            .point(200)
            .social(Social.KAKAO)
            .profileImage("profileImage")
            .uuid("uuid")
            .deletedAt(null)
            .group(userGroup)
            .deviceToken("deviceToken")
            .subscribe(Subscribe.NORMAL)
            .ticketCount(0)
            .groupAdmissionYear(20)
            .email("test@test.com")
            .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            user.getYelloId(),
            null,
            List.of(new SimpleGrantedAuthority("USER"))
        );

        authentication.setDetails(user);

        context.setAuthentication(authentication);
        return context;
    }
}
