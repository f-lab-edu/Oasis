package com.flab.oasis.config.mybatis;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.flab.oasis.mapper.feed", sqlSessionFactoryRef = "feedSqlSessionFactory")
@RequiredArgsConstructor
public class FeedConfig {
    private final ApplicationContext applicationContext;

    @Bean("feedHikariConfig")
    @ConfigurationProperties("spring.datasource.feed.hikari")
    public HikariConfig feedHikariConfig() {
        return new HikariConfig();
    }

    @Bean("feedDataSource")
    public HikariDataSource feedDataSource(@Qualifier("feedHikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean("feedSqlSessionFactory")
    public SqlSessionFactory feedSqlSessionFactory(@Qualifier("feedDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(
                applicationContext.getResources("classpath:/mapper/feed/*.xml")
        );

        return sqlSessionFactoryBean.getObject();
    }
}
