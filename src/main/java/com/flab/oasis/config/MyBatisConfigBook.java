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
@MapperScan(basePackages = "com.flab.oasis.mapper.book", sqlSessionFactoryRef = "bookSqlSessionFactory")
@RequiredArgsConstructor
public class MyBatisConfigBook {
    private final ApplicationContext applicationContext;

    @Bean("bookHikariConfig")
    @ConfigurationProperties("spring.datasource.book.hikari")
    public HikariConfig bookHikariConfig() {
        return new HikariConfig();
    }

    @Bean("bookHikariDataSource")
    public HikariDataSource bookDataSource(HikariConfig bookHikariConfig) {
        return new HikariDataSource(bookHikariConfig);
    }

    @Bean
    public SqlSessionFactory bookSqlSessionFactory(HikariDataSource bookHikariDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(bookHikariDataSource);
        sqlSessionFactoryBean.setMapperLocations(
                applicationContext.getResources("classpath:/mapper/book/*.xml")
        );

        return sqlSessionFactoryBean.getObject();
    }
}
