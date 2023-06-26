package com.yello.server.repository;

import com.yello.server.entity.User;
import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User, Long> {
    // CREATE
}
