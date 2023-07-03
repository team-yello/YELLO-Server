package com.yello.server.domain.friend.repository;

import com.yello.server.domain.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}
