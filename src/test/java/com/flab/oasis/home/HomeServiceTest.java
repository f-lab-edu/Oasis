package com.flab.oasis.home;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.mapper.UserAuthMapper;
import com.flab.oasis.mapper.UserCategoryMapper;
import com.flab.oasis.mapper.UserInfoMapper;
import com.flab.oasis.model.*;
import com.flab.oasis.service.HomeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Objects;

@SpringBootTest
@AutoConfigureMockMvc
class HomeServiceTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    HomeService homeService;

    @Autowired
    UserAuthMapper userAuthMapper;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserCategoryMapper userCategoryMapper;

    @Autowired
    EhCacheCacheManager ehCacheCacheManager;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        String uid = "test@naver.com";
        userAuthMapper.insertUserAuth(
                new UserAuth(uid, "1234", 'N', null, null)
        );
        userInfoMapper.insertUserInfo(
                new UserInfo(uid, "test_user", "hello world!", "www.test.com")
        );
        for (int categoryId : new int[] {101, 102, 103}) {
            userCategoryMapper.insertUserCategory(
                    new UserCategory(uid, categoryId)
            );
        }
    }

    @AfterEach
    void clear() {
        String uid = "test@naver.com";
        userCategoryMapper.deleteUserCategoryByUid(uid);
        userInfoMapper.deleteUserInfoByUid(uid);
        userAuthMapper.deleteUserAuthByUid(uid);
    }

    @DisplayName("/home/suggestion request 결과 비교")
    @Test
    void suggestionTest() throws Exception {
        String uid = "test@naver.com";
        BookSuggestionRequest bookSuggestionRequest = new BookSuggestionRequest(uid, SuggestionType.NEWBOOK);
        String expected = objectMapper.writeValueAsString(homeService.suggestion(bookSuggestionRequest));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/home/suggestion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookSuggestionRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(expected));
    }

    @DisplayName("캐싱된 값과 실제 결과 값 비교")
    @Test
    void ehCacheTest() {
        String uid = "test@naver.com";
        List<BookSuggestion> bookList =  homeService.suggestion(
                new BookSuggestionRequest(uid, SuggestionType.NEWBOOK)
        );

        SimpleValueWrapper bookCache =
                (SimpleValueWrapper) Objects.requireNonNull(ehCacheCacheManager.getCache("homeCache"))
                        .get(String.format("%s_%s_%s", "suggestion", uid, SuggestionType.NEWBOOK));

        Assertions.assertEquals(bookList, Objects.requireNonNull(bookCache).get());
    }
}
