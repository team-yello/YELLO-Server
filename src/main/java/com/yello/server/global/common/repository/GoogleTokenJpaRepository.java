package com.yello.server.global.common.repository;

import com.yello.server.global.common.entity.GoogleToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoogleTokenJpaRepository extends JpaRepository<GoogleToken, Long> {

}
