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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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

    @DisplayName("/home/suggestion POST 요청")
    @Test
    void testRequestSuggestion() throws Exception {
        String uid = "test@naver.com";
        SuggestionType suggestionType = SuggestionType.NEWBOOK;

        ObjectMapper objectMapper = new ObjectMapper();
        BookSuggestionRequest bookSuggestionRequest = new BookSuggestionRequest(uid, suggestionType);
        List<BookSuggestion> bookSuggestionList = generateBookSuggestionList(suggestionType);

        BDDMockito.given(homeService.getBookSuggestionListByBookSuggestionRequest(bookSuggestionRequest))
                .willReturn(bookSuggestionList);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/home/suggestion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookSuggestionRequest))
                )
                .andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[0].title")
                                .value(bookSuggestionList.get(0).getTitle())
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
