package com.yello.server.domain.user.repository;

import com.yello.server.domain.user.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataJpaRepository extends JpaRepository<UserData, Long> {

}
