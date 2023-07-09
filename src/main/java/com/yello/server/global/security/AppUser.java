package com.yello.server.global.security;

import java.util.Collection;

import com.yello.server.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.collect.ImmutableList;

@SuppressWarnings("serial")
public class AppUser implements UserDetails {
    private User user;

    private final Collection<GrantedAuthority> authorities;

    public AppUser(User user) {
        this.user = user;
        this.authorities = new ImmutableList.Builder<GrantedAuthority>().add(new SimpleGrantedAuthority("ROLE_APP"))
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
