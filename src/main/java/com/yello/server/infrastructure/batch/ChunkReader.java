package com.yello.server.infrastructure.batch;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserJpaRepository;
import jakarta.persistence.EntityManagerFactory;
import java.util.Collections;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

@RequiredArgsConstructor
@Configuration
public class ChunkReader {

    private final UserJpaRepository userRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final DataSource dataSource;

    @Bean
    @StepScope
    public RepositoryItemReader<User> usersDataRepositoryItemReader() {

        return new RepositoryItemReaderBuilder<User>()
            .name("userDataReader")
            .repository(userRepository)
            .methodName("findAllByPageable")
            .pageSize(100)
            .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
            .build();
    }

    public JdbcCursorItemReader<User> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<User>()
            .fetchSize(10)
            .dataSource(dataSource)
            .rowMapper(new BeanPropertyRowMapper<>(User.class))
            .sql("SELECT u.id, u.name FROM user u WHERE u.deleted_at is NULL ORDER BY u.id")
            .name("jdbcCursorItemReader")
            .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<User> userDataJpaPagingItemReader() {

        return new JpaPagingItemReaderBuilder<User>()
            .name("userDataReader")
            .pageSize(100)
            .queryString("SELECT u FROM USER u ORDER BY id")
            .entityManagerFactory(entityManagerFactory)
            .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<User> userDataJdbcPagingItemReader() throws Exception {

        return new JdbcPagingItemReaderBuilder<User>()
            .pageSize(100)
            .fetchSize(100)
            .dataSource(dataSource)
            .queryProvider(createUserDataQueryProvider())
            .rowMapper(new BeanPropertyRowMapper<>(User.class))
            .name("jdbcPagingItemReader")
            .build();
    }

    @Bean
    public PagingQueryProvider createUserDataQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("*");
        queryProvider.setFromClause("from user");
        queryProvider.setWhereClause("where deleted_at is null");

        queryProvider.setSortKeys(Collections.singletonMap("id", Order.ASCENDING));

        return queryProvider.getObject();

    }
}
