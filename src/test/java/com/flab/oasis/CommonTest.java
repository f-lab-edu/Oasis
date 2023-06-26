package com.flab.oasis;

import com.flab.oasis.config.MyBatisConfigBook;
import com.flab.oasis.mapper.book.TestMapper;
import com.flab.oasis.service.TestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(MyBatisConfigBook.class)
class CommonTest {
    @Autowired
    TestMapper testMapper;

    @InjectMocks
    TestService testService;

    @Mock
    TestMapper mockTestMapper;

    @DisplayName("MyBatis Connection 테스트")
    @Test
    void myBatisConnectionTest() {
        Assertions.assertEquals(10, testMapper.getTestData());
    }

    @DisplayName("Redis Caching 테스트")
    @Test
    void redisCachingTest() {
        BDDMockito.given(mockTestMapper.getTestData()).willReturn(0);

        IntStream.range(0, 1).forEach(i -> testService.dataCachingByRedis());

        // getTestData()가 실제로 1회만 동작했는지 확인
        BDDMockito.then(mockTestMapper).should(BDDMockito.times(1)).getTestData();
    }

    @DisplayName("Redis Cache Evict 테스트")
    @Test
    void redisCacheEvictTest() {
        BDDMockito.given(mockTestMapper.getTestData()).willReturn(0);

        testService.dataCachingByRedis();

        testService.cacheEvictInRedis();

        testService.dataCachingByRedis();

        // getTestData()가 실제로 2회 동작했는지 확인
        BDDMockito.then(mockTestMapper).should(BDDMockito.times(2)).getTestData();
    }

    @DisplayName("EhCache Caching 테스트")
    @Test
    void ehCacheCachingTest() {
        BDDMockito.given(mockTestMapper.getTestData()).willReturn(0);

        IntStream.range(0, 1).forEach(i -> testService.dataCachingByEhCache());

        // getTestData()가 실제로 1회만 동작했는지 확인
        BDDMockito.then(mockTestMapper).should(BDDMockito.times(1)).getTestData();
    }

    @DisplayName("EhCache Cache Evict 테스트")
    @Test
    void ehCacheCacheEvictTest() {
        BDDMockito.given(mockTestMapper.getTestData()).willReturn(0);

        testService.dataCachingByEhCache();

        testService.cacheEvictInEhCache();

        testService.dataCachingByEhCache();

        // getTestData()가 실제로 2회 동작했는지 확인
        BDDMockito.then(mockTestMapper).should(BDDMockito.times(2)).getTestData();
    }
}
