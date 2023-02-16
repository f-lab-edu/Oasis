package com.flab.oasis.home;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.controller.HomeController;
import com.flab.oasis.model.BookSuggestion;
import com.flab.oasis.model.BookSuggestionRequest;
import com.flab.oasis.service.HomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {
    @InjectMocks
    HomeController homeController;

    @Mock
    HomeService homeService;

    MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }

    @DisplayName("/home/suggestion request 결과 비교")
    @Test
    void suggestionTest() throws Exception {
        String uid = "test@naver.com";
        SuggestionType suggestionType = SuggestionType.NEWBOOK;

        ObjectMapper objectMapper = new ObjectMapper();
        BookSuggestionRequest bookSuggestionRequest = new BookSuggestionRequest(uid, suggestionType);
        List<BookSuggestion> bookSuggestionList = generateBookSuggestionList(suggestionType);

        BDDMockito.given(homeService.suggestion(bookSuggestionRequest)).willReturn(bookSuggestionList);

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/home/suggestion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookSuggestionRequest))
        );

        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookSuggestionList)));

    }

    private static List<BookSuggestion> generateBookSuggestionList(SuggestionType suggestionType) {
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
}
