package com.yello.server.domain.user.repository;

import com.yello.server.domain.user.entity.UserPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPostJpaRepository extends JpaRepository<UserPost, Long> {

}
