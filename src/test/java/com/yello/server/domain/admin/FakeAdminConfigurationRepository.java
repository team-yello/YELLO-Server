package com.yello.server.domain.admin;

import com.yello.server.domain.admin.entity.AdminConfiguration;
import com.yello.server.domain.admin.entity.AdminConfigurationType;
import com.yello.server.domain.admin.repository.AdminConfigurationRepository;
import java.util.ArrayList;
import java.util.List;

public class FakeAdminConfigurationRepository implements AdminConfigurationRepository {

    private List<AdminConfiguration> data = new ArrayList<>();
    private Long id = 0L;

    private AdminConfiguration jpaSave(AdminConfiguration adminConfiguration) {
        if (adminConfiguration.getId() != null && adminConfiguration.getId() > id) {
            id = adminConfiguration.getId();
        }

        AdminConfiguration newConfiguration = AdminConfiguration.builder()
            .id(adminConfiguration.getId() == null ? ++id : adminConfiguration.getId())
            .tag(adminConfiguration.getTag())
            .value(adminConfiguration.getValue())
            .build();

        data.add(newConfiguration);
        return newConfiguration;
    }

    @Override
    public List<AdminConfiguration> findConfigurations(AdminConfigurationType tag) {
        return data.stream()
            .filter(adminConfiguration -> adminConfiguration.getTag().equals(tag))
            .toList();
    }

    @Override
    public void setConfigurations(AdminConfigurationType tag, String value) {
        final List<AdminConfiguration> configurations = findConfigurations(tag);

        if (configurations.isEmpty()) {
            jpaSave(AdminConfiguration.builder()
                .tag(tag)
                .value(value)
                .build());

            return;
        }

        for (int i = 0; i < configurations.size(); i++) {
            final AdminConfiguration configuration = data.get(i);

            if (configuration.getTag().equals(tag)) {
                data.set(i,
                    AdminConfiguration.builder()
                        .id(configuration.getId())
                        .tag(tag)
                        .value(value)
                        .build()
                );
            }
        }
    }

    @Override
    public void deleteConfigurations(AdminConfigurationType tag) {
        data = data.stream()
            .filter(adminConfiguration -> !adminConfiguration.getTag().equals(tag))
            .toList();
    }
}
