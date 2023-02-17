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

        List<UserCategory> userCategoryList = new ArrayList<>();
        UserCategory userCategory = new UserCategory();
        userCategory.setUid(uid);
        userCategory.setCategoryId(101);

        userCategoryList.add(userCategory);

        BDDMockito.given(userCategoryMapper.findUserCategoryByUid(uid))
                .willReturn(userCategoryList);
        BDDMockito.given(bookSuggestionRepository.getBookSuggestionList(suggestionType))
                .willReturn(generateBookSuggestionList(suggestionType));

        Assertions.assertEquals(
                userCategoryList.get(0).getCategoryId(),
                homeService.suggestion(new BookSuggestionRequest(uid, suggestionType)).get(0).getCategoryId()
        );
    }

    @DisplayName("User Category가 존재하지 않을 때")
    @Test
    void 유저_카테고리가_없을_때_모든_카테고리를_반환() {
        String uid = "test@naver.com";
        SuggestionType suggestionType = SuggestionType.NEWBOOK;
        List<BookSuggestion> bookSuggestionList = generateBookSuggestionList(suggestionType);

        BDDMockito.given(userCategoryMapper.findUserCategoryByUid(uid))
                .willReturn(new ArrayList<>());
        BDDMockito.given(bookSuggestionRepository.getBookSuggestionList(suggestionType))
                .willReturn(bookSuggestionList);

        Assertions.assertEquals(
                bookSuggestionList.get(0).getCategoryId(),
                homeService.suggestion(new BookSuggestionRequest(uid, suggestionType)).get(0).getCategoryId()
        );
    }

    private static List<BookSuggestion> generateBookSuggestionList(SuggestionType suggestionType) {
        List<BookSuggestion> bookSuggestionList = new ArrayList<>();
        BookSuggestion bookSuggestion = new BookSuggestion();
        bookSuggestion.setSuggestionType(suggestionType);
        bookSuggestion.setBookId("1234");
        bookSuggestion.setTitle("title");
        bookSuggestion.setAuthor("author");
        bookSuggestion.setTranslator("trans");
        bookSuggestion.setPublisher("publish");
        bookSuggestion.setPublishDate(new Date());
        bookSuggestion.setCategoryId(101);
        bookSuggestion.setCategoryName("category name");
        bookSuggestion.setDescription("desc");
        bookSuggestion.setImageUrl("url");

        bookSuggestionList.add(bookSuggestion);

        return bookSuggestionList;
    }
}
