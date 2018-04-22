package codesquad.service;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.web.QuestionAcceptanceTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void create() throws Exception {

        User loginUser = new User(100, "user11", "pw11", "name11", "email11");
        QuestionDto questionDto = new QuestionDto("제목", "본문내용");
        Question mockQuestion = new Question(questionDto.getTitle(), questionDto.getContents());
        mockQuestion.writeBy(loginUser);
        when(questionRepository.save(questionDto.toQuestion())).thenReturn(mockQuestion);

        Question question = qnaService.create(loginUser, questionDto);

        final Question expected = questionDto.toQuestion();
        assertThat(question.getTitle(), is(expected.getTitle()));
        assertThat(question.getContents(), is(expected.getContents()));
        assertThat(question.getWriter(), is(loginUser));
    }
}
