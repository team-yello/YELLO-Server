package com.yello.server.domain.admin.repository;

import com.yello.server.domain.admin.entity.AdminConfiguration;
import com.yello.server.domain.admin.entity.AdminConfigurationType;
import java.util.List;

public interface AdminConfigurationRepository {

    List<AdminConfiguration> findConfigurations(AdminConfigurationType tag);

    void setConfigurations(AdminConfigurationType tag, String value);

    void deleteConfigurations(AdminConfigurationType tag);
}
