package com.yello.server.domain.admin.repository;

import com.yello.server.domain.admin.entity.AdminConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminConfigurationJpaRepository extends JpaRepository<AdminConfiguration, Long> {

}
