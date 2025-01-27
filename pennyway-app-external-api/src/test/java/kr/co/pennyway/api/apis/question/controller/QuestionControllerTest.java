package kr.co.pennyway.api.apis.question.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pennyway.api.apis.question.dto.QuestionReq;
import kr.co.pennyway.api.apis.question.usecase.QuestionUseCase;
import kr.co.pennyway.api.config.WebConfig;
import kr.co.pennyway.domain.domains.question.domain.QuestionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = QuestionController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)})
@ActiveProfiles("local")
public class QuestionControllerTest {

    private final String expectedEmail = "test@gmail.com";
    private final String expectedContent = "test question content";
    private final QuestionCategory expectedCategory = QuestionCategory.ETC;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private QuestionUseCase questionUseCase;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .defaultRequest(post("/**"))//.with(csrf()))
                .build();
    }

    @Test
    @DisplayName("[1] 이메일, 내용을 필수로 입력해야 합니다.")
    void requiredInputError() throws Exception {
        // given
        QuestionReq request = new QuestionReq("", "", QuestionCategory.ETC);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/v1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        resultActions
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors.email").value("이메일을 입력해주세요"))
                .andExpect(jsonPath("$.fieldErrors.content").value("문의 내용은 1자 이상 5000자 이하로 입력해주세요"))
                .andDo(print());
    }

    @Test
    @DisplayName("[2] 이메일 형식 오류입니다.")
    void emailValidError() throws Exception {
        // given
        QuestionReq request = new QuestionReq("test", "test question content", QuestionCategory.ETC);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/v1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        resultActions
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors.email").value("이메일 형식이 올바르지 않습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName(("[3] 문의 카테고리를 선택해주세요."))
    void categoryMissingError() throws Exception {
        // given
        QuestionReq request = new QuestionReq("team.collabu@gmail.com", "test question content", null);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/v1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        resultActions
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors.category").value("문의 카테고리를 입력해주세요"))
                .andDo(print());
    }

    @Test
    @DisplayName("[4] 정상적인 문의 요청입니다.")
    void sendQuestion() throws Exception {
        // given
        QuestionReq request = new QuestionReq(expectedEmail, expectedContent, expectedCategory);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/v1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[5] 이메일, 내용을 필수로 입력해야 합니다.")
    void sendLargeQuestion() throws Exception {

        // given
        String content = "a".repeat(5001);
        QuestionReq request = new QuestionReq("team.collabu@gmail.com", content, QuestionCategory.ETC);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/v1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        resultActions
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors.content").value("문의 내용은 1자 이상 5000자 이하로 입력해주세요"))
                .andDo(print());
    }
}
