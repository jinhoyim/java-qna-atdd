package codesquad.web;

import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final String API_QUESTION_URI = "/api/questions";

    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Test
    public void create() {
        QuestionDto newQuestion = createQuestionDto(10);
        final String location = createResource(API_QUESTION_URI, newQuestion, defaultUser());

        final QuestionDto created = getResource(location, QuestionDto.class);
        assertThat(created.getTitle(), is(newQuestion.getTitle()));
        assertThat(created.getContents(), is(newQuestion.getContents()));
    }

    @Test
    public void create_no_login() {
        QuestionDto newQuestion = createQuestionDto(11);

        final ResponseEntity<String> response = template().postForEntity(API_QUESTION_URI, newQuestion, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private QuestionDto createQuestionDto(long id) {
        return createQuestionDto(id, "질문제목", "질문본문");
    }

    private QuestionDto createQuestionDto(long id, String title, String contents) {
        return new QuestionDto(id, title, contents);
    }

    @Test
    public void read() {
        final QuestionDto questionDto = defaultQuestion().toQuestionDto();
        String location = API_QUESTION_URI + "/" + questionDto.getId();
        final QuestionDto found = getResource(location, QuestionDto.class);
        assertThat(found, is(questionDto));
    }

    @Test
    public void update() {
        QuestionDto newQuestion = createQuestionDto(12);
        String location = createResource(API_QUESTION_URI, newQuestion, defaultUser());
        final QuestionDto created = getResource(location, QuestionDto.class);
        QuestionDto updateQuestion = createQuestionDto(created.getId(), "수정질문", "수정답변");

        basicAuthTemplate(defaultUser()).put(location, updateQuestion);

        final QuestionDto updated = getResource(location, QuestionDto.class);

        assertThat(updated, is(updateQuestion));
    }

    @Test
    public void update_다른_사람() {
        User writer = findByUserId(SECOND_LOGIN_USER);
        QuestionDto newQuestion = createQuestionDto(12);
        String location = createResource(API_QUESTION_URI, newQuestion, writer);
        final QuestionDto created = getResource(location, QuestionDto.class);
        QuestionDto updateQuestion = createQuestionDto(created.getId(), "수정질문", "수정답변");

        final ResponseEntity<Void> updateResponse = basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, new HttpEntity<>(newQuestion), Void.class);
        assertThat(updateResponse.getStatusCode(), is(HttpStatus.FORBIDDEN));

        final QuestionDto updated = getResource(location, QuestionDto.class);
        assertThat(updated, not(updateQuestion));
        assertThat(updated, is(created));
    }

    @Test
    public void delete() {
        QuestionDto newQuestion = createQuestionDto(13);
        String location = createResource(API_QUESTION_URI, newQuestion, defaultUser());
        final QuestionDto created = getResource(location, QuestionDto.class);

        basicAuthTemplate(defaultUser()).delete(location);

        final QuestionDto deleted = getResource(location, QuestionDto.class);

        assertThat(created, notNullValue());
        assertThat(deleted, nullValue());
    }

    @Test
    public void delete_other_user() {
        User writer = findByUserId(SECOND_LOGIN_USER);
        QuestionDto newQuestion = createQuestionDto(14);
        String location = createResource(API_QUESTION_URI, newQuestion, writer);
        final QuestionDto created = getResource(location, QuestionDto.class);

        final ResponseEntity<Void> updateResponse = basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);
        assertThat(updateResponse.getStatusCode(), is(HttpStatus.FORBIDDEN));

        final QuestionDto deleted = getResource(location, QuestionDto.class);
        assertThat(deleted, is(created));
    }

    @Test
    public void list() {
        QuestionsDto beforeCreate = getResource(API_QUESTION_URI, QuestionsDto.class);

        QuestionDto newQuestion = createQuestionDto(15);
        createResource(API_QUESTION_URI, newQuestion, defaultUser());

        QuestionsDto afterCreate = getResource(API_QUESTION_URI, QuestionsDto.class);

        assertThat(afterCreate.getSize(), is(beforeCreate.getSize() + 1));
    }
}