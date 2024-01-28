package com.yello.server.domain.admin.repository;

import static com.yello.server.domain.admin.entity.QAdminConfiguration.adminConfiguration;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yello.server.domain.admin.entity.AdminConfiguration;
import com.yello.server.domain.admin.entity.AdminConfigurationType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminConfigurationRepositoryImpl implements AdminConfigurationRepository {

    private final AdminConfigurationJpaRepository adminConfigurationJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<AdminConfiguration> getConfigurations(AdminConfigurationType tag) {
        return jpaQueryFactory
            .selectFrom(adminConfiguration)
            .where(adminConfiguration.tag.eq(tag))
            .fetch();
    }

    @Transactional
    @Override
    public void setConfigurations(AdminConfigurationType tag, String value) {
        final List<AdminConfiguration> configurations = jpaQueryFactory
            .selectFrom(adminConfiguration)
            .where(adminConfiguration.tag.eq(tag))
            .fetch();

        if (configurations.isEmpty()) {
            adminConfigurationJpaRepository.save(
                AdminConfiguration.builder()
                    .tag(tag)
                    .value(value)
                    .build()
            );
            return;
        }

        jpaQueryFactory
            .update(adminConfiguration)
            .set(adminConfiguration.value, value)
            .where(adminConfiguration.tag.eq(tag))
            .execute();
    }

    @Transactional
    @Override
    public void deleteConfigurations(AdminConfigurationType tag) {
        jpaQueryFactory
            .delete(adminConfiguration)
            .where(adminConfiguration.tag.eq(tag))
            .execute();
    }
}
