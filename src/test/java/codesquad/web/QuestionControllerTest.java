package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.service.QnaService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
public class QuestionControllerTest {
    @Mock
    private QnaService qnaService;

    @InjectMocks
    private QuestionController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void form() throws Exception {
        mockMvc.perform(get("/qna/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("/qna/form"));
    }

    @Test
    public void create() throws Exception {
        User user = new User(5, "", "", "", "");
        QuestionDto questionDto = new QuestionDto("t", "c");
        mockMvc.perform(post("/qna", user, questionDto))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/home"));

        verify(qnaService, times(1)).create(user, questionDto);
        verifyNoMoreInteractions(qnaService);
    }

    @Test
    public void read() throws Exception {
        long id = 1;
        Question question = new Question("질문제목", "내용");

        when(qnaService.findById(id)).thenReturn(question);
        mockMvc.perform(get("/qna/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("/qna/show"))
                .andExpect(model().attributeExists("question"))
                .andExpect(model().attribute("question", question));


        verify(qnaService, times(1)).findById(id);
        verifyNoMoreInteractions(qnaService);
    }

    @Test
    public void updateForm() throws Exception {
        User user = new User(5, "", "", "", "");
        long id = 1;
        Question question = new Question("질문제목", "내용");

        when(qnaService.findOwnedById(user, id)).thenReturn(question);
        mockMvc.perform(get("/qna/{id}/form", id))
                .andExpect(status().isOk())
                .andExpect(view().name("/qna/updateForm"))
                .andExpect(model().attributeExists("question"))
                .andExpect(model().attribute("question", question));


    }
}
