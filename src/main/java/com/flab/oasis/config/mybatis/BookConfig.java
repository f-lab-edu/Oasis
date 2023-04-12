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
@MapperScan(basePackages = "com.flab.oasis.mapper.book", sqlSessionFactoryRef = "bookSqlSessionFactory")
@RequiredArgsConstructor
public class BookConfig {
    private final ApplicationContext applicationContext;

    @Bean("bookHikariConfig")
    @ConfigurationProperties("spring.datasource.book.hikari")
    public HikariConfig bookHikariConfig() {
        return new HikariConfig();
    }

    @Bean("bookDataSource")
    public HikariDataSource bookDataSource(@Qualifier("bookHikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean("bookSqlSessionFactory")
    public SqlSessionFactory bookSqlSessionFactory(@Qualifier("bookDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(
                applicationContext.getResources("classpath:/mapper/book/*.xml")
        );

        return sqlSessionFactoryBean.getObject();
    }
}
