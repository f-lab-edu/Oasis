package com.flab.oasis;

import com.flab.oasis.model.Book;
import com.flab.oasis.model.TestModel;
import com.flab.oasis.service.TestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class ConnectionTest {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    TestService testService;

    @Test
    void redisTest() {
        redisTemplate.opsForHash().put("home", "test1", "value1");
        redisTemplate.opsForHash().put("home", "test2", "value2");
        redisTemplate.opsForHash().put("home", "test3", "value3");

        Assertions.assertEquals("value2", redisTemplate.opsForHash().get("home", "test2"));
    }

    @Test
    void dbTest() {
        TestModel testModel = testService.dbConnectionTest();

        Assertions.assertEquals(10, testModel.getData());
    }

    @Test
    void ehCacheTest() {
        Book book = testService.cacheTest();
        book = testService.cacheTest();

        Assertions.assertNotNull(book);
    }
}
