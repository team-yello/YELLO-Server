package com.yello.server.Repository;

import com.yello.server.entity.User;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    // CREATE
}
