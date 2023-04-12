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
@MapperScan(basePackages = "com.flab.oasis.mapper.user", sqlSessionFactoryRef = "userSqlSessionFactory")
@RequiredArgsConstructor
public class UserConfig {
    private final ApplicationContext applicationContext;

    @Bean("userHikariConfig")
    @ConfigurationProperties("spring.datasource.user.hikari")
    public HikariConfig userHikariConfig() {
        return new HikariConfig();
    }

    @Bean("userDataSource")
    public HikariDataSource userDataSource(@Qualifier("userHikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean("userSqlSessionFactory")
    public SqlSessionFactory userSqlSessionFactory(@Qualifier("userDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(
                applicationContext.getResources("classpath:/mapper/user/*.xml")
        );

        return sqlSessionFactoryBean.getObject();
    }
}
