package codesquad.service;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
        QuestionDto questionDto = new QuestionDto("질문제목", "본문내용");
        Question mockQuestion = questionDto.toQuestion();
        mockQuestion.writeBy(loginUser);
        when(questionRepository.save(questionDto.toQuestion())).thenReturn(mockQuestion);

        Question question = qnaService.create(loginUser, questionDto);

        assertThat(question.getTitle(), is(questionDto.getTitle()));
        assertThat(question.getContents(), is(questionDto.getContents()));
        assertThat(question.getWriter(), is(loginUser));
        verify(questionRepository, times(1)).save(questionDto.toQuestion());
    }

    @Test
    public void findById() {
        long questionId = 10;
        String title = "질문제목";
        String contents = "본문내용";
        Question mockQuestion = new Question(title, contents);
        when(questionRepository.findOne(questionId)).thenReturn(mockQuestion);

        final Question question = qnaService.findById(questionId);

        assertThat(question.getTitle(), is(title));
        assertThat(question.getContents(), is(contents));
        verify(questionRepository, times(1)).findOne(10L);
    }

    @Test(expected = UnAuthorizedException.class)
    public void findOwnedById_failed_when_mismatch_writer() {
        User loginUser = new User(10, "user11", "pw11", "name11", "email11");
        User writer = new User(20, "writer-id", "pw11", "writer-name", "writer-email");
        long questionId = 10;
        String title = "질문제목";
        String contents = "본문내용";
        Question mockQuestion = new Question(title, contents);
        mockQuestion.writeBy(writer);
        when(questionRepository.findOne(questionId)).thenReturn(mockQuestion);

        qnaService.findOwnedById(loginUser, questionId);
    }

    @Test
    public void findOwnedById() {
        User loginUser = new User(10, "user11", "pw11", "name11", "email11");
        User writer = new User(10, "user11", "pw11", "name11", "email11");
        long questionId = 10;
        String title = "질문제목";
        String contents = "본문내용";
        Question mockQuestion = new Question(title, contents);
        mockQuestion.writeBy(writer);
        when(questionRepository.findOne(questionId)).thenReturn(mockQuestion);

        qnaService.findOwnedById(loginUser, questionId);
    }

    @Test
    public void update() {
        User loginUser = new User(10, "user11", "pw11", "name11", "email11");
        long questionId = 10;
        Question originalQuestion = new Question(10, "질문제목", "본문내용");
        originalQuestion.writeBy(loginUser);
        Question target = new Question("수정제목", "수정본문");

        final Question updatedQuestion = originalQuestion.update(loginUser, target);

        when(questionRepository.findOne(questionId)).thenReturn(originalQuestion);
        when(questionRepository.save(updatedQuestion)).thenReturn(updatedQuestion);

        final Question updatedResult = qnaService.update(loginUser, questionId, target);

        assertThat(updatedQuestion, is(updatedResult));
        verify(questionRepository, times(1)).findOne(questionId);
        verify(questionRepository, times(1)).save(updatedQuestion);
    }
}
