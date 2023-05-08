package com.flab.oasis.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.flab.oasis.mapper.user", sqlSessionFactoryRef = "userSqlSessionFactory")
@RequiredArgsConstructor
public class MyBatisConfigUser {
    private final ApplicationContext applicationContext;

    @Bean("userHikariConfig")
    @ConfigurationProperties("spring.datasource.user.hikari")
    public HikariConfig userHikariConfig() {
        return new HikariConfig();
    }

    @Bean("userHikariDataSource")
    public HikariDataSource userDataSource(HikariConfig userHikariConfig) {
        return new HikariDataSource(userHikariConfig);
    }

    @Bean
    public SqlSessionFactory userSqlSessionFactory(HikariDataSource userHikariDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(userHikariDataSource);
        sqlSessionFactoryBean.setMapperLocations(
                applicationContext.getResources("classpath:/mapper/user/*.xml")
        );

        return sqlSessionFactoryBean.getObject();
    }
}
