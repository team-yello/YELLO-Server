package com.yello.server.entity.user;

import com.yello.server.entity.user.User;
import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User, Long> {
    // CREATE
}
