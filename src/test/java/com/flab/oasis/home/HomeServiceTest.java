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

    @DisplayName("User Category가 존재할 때")
    @Test
    void 유저_카테고리에_해당하는_카테고리만_반환() {
        String uid = "test@naver.com";
        SuggestionType suggestionType = SuggestionType.NEWBOOK;
        int categoryCount = 1;
        int bookSuggestionCount = 2;

        BDDMockito.given(userCategoryMapper.findUserCategoryByUid(uid))
                .willReturn(generateUserCategory(uid, categoryCount));
        BDDMockito.given(bookSuggestionRepository.getBookSuggestionList(suggestionType))
                .willReturn(generateBookSuggestionList(suggestionType, bookSuggestionCount));

        Assertions.assertEquals(
                categoryCount, homeService.suggestion(new BookSuggestionRequest(uid, suggestionType)).size()
        );
    }

    @DisplayName("User Category가 존재하지 않을 때")
    @Test
    void 유저_카테고리가_없을_때_모든_카테고리를_반환() {
        String uid = "test@naver.com";
        SuggestionType suggestionType = SuggestionType.NEWBOOK;
        int categoryCount = 0;
        int bookSuggestionCount = 1;

        BDDMockito.given(userCategoryMapper.findUserCategoryByUid(uid))
                .willReturn(generateUserCategory(uid, categoryCount));
        BDDMockito.given(bookSuggestionRepository.getBookSuggestionList(suggestionType))
                .willReturn(generateBookSuggestionList(suggestionType, bookSuggestionCount));

        Assertions.assertEquals(
                bookSuggestionCount, homeService.suggestion(new BookSuggestionRequest(uid, suggestionType)).size()
        );
    }

    private List<BookSuggestion> generateBookSuggestionList(SuggestionType suggestionType, int bookSuggestionCount) {
        List<BookSuggestion> bookSuggestionList = new ArrayList<>();
        for (int i = 0; i < bookSuggestionCount; i++) {
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
}
