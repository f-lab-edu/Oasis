package com.flab.oasis.home;

import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.mapper.UserCategoryMapper;
import com.flab.oasis.model.BookSuggestion;
import com.flab.oasis.model.BookSuggestionRequest;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.repository.BookSuggestionRepository;
import com.flab.oasis.service.HomeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class HomeServiceTest {
    @InjectMocks
    HomeService homeService;

    @Mock
    BookSuggestionRepository bookSuggestionRepository;

    @Mock
    UserCategoryMapper userCategoryMapper;

    @Mock
    EhCacheCacheManager ehCacheCacheManager;

    @DisplayName("/home/suggestion request 결과 비교")
    @Test
    void suggestionTest() throws Exception {
        String uid = "test@naver.com";
        SuggestionType suggestionType = SuggestionType.NEWBOOK;
        int categoryCount = 3;

        BDDMockito.given(userCategoryMapper.findUserCategoryByUid(uid))
                .willReturn(generateUserCategory(uid, categoryCount));
        BDDMockito.given(bookSuggestionRepository.getBookSuggestionList(suggestionType))
                .willReturn(generateBookSuggestionList(suggestionType));

        Assertions.assertEquals(
                categoryCount, homeService.suggestion(generateBookSuggestionRequest(uid, suggestionType)).size()
        );
    }

    private BookSuggestionRequest generateBookSuggestionRequest(String uid, SuggestionType suggestionType) {
        BookSuggestionRequest bookSuggestionRequest = new BookSuggestionRequest();
        bookSuggestionRequest.setUid(uid);
        bookSuggestionRequest.setSuggestionType(SuggestionType.NEWBOOK);

        return bookSuggestionRequest;
    }

    private List<BookSuggestion> generateBookSuggestionList(SuggestionType suggestionType) {
        List<BookSuggestion> bookSuggestionList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BookSuggestion bookSuggestion = new BookSuggestion();
            bookSuggestion.setSuggestionType(suggestionType);
            bookSuggestion.setBookId(String.format("%s%d", "1234", i));
            bookSuggestion.setTitle(String.format("%s%d", "title", i));
            bookSuggestion.setAuthor(String.format("%s%d", "author", i));
            bookSuggestion.setTranslator(String.format("%s%d", "trans", i));
            bookSuggestion.setPublisher(String.format("%s%d", "publish", i));
            bookSuggestion.setPublishDate(new Date());
            bookSuggestion.setCategoryId(101 + i);
            bookSuggestion.setCategoryName("category name");
            bookSuggestion.setDescription(String.format("%s%d", "desc", i));
            bookSuggestion.setImageUrl(String.format("%s%d", "url", i));

            bookSuggestionList.add(bookSuggestion);
        }

        return bookSuggestionList;
    }

    private List<UserCategory> generateUserCategory(String uid, int categoryCount) {
        List<UserCategory> userCategoryList = new ArrayList<>();
        for (int i = 0; i < categoryCount; i++) {
            UserCategory userCategory = new UserCategory();
            userCategory.setUid(uid);
            userCategory.setCategoryId(101 + i);

            userCategoryList.add(userCategory);
        }

        return userCategoryList;
    }

//    @DisplayName("캐싱된 값과 실제 결과 값 비교")
//    @Test
//    void ehCacheTest() {
//        String uid = "test@naver.com";
//        SuggestionType suggestionType = SuggestionType.NEWBOOK;
//        int categoryCount = 3;
//
//        BDDMockito.given(userCategoryMapper.findUserCategoryByUid(uid))
//                .willReturn(generateUserCategoryMock(uid, categoryCount));
//        BDDMockito.given(bookSuggestionRepository.getBookSuggestionList(suggestionType))
//                .willReturn(generateBookSuggestionListMock(suggestionType));
//
//        List<BookSuggestion> expected =  homeService.suggestion(generateBookSuggestionRequest(uid, suggestionType));
//
//        SimpleValueWrapper actual =
//                (SimpleValueWrapper) Objects.requireNonNull(ehCacheCacheManager.getCache("homeCache"))
//                        .get(String.format("%s_%s_%s", "suggestion", uid, suggestionType));
//
//        Assertions.assertEquals(expected, Objects.requireNonNull(actual).get());
//    }
}
