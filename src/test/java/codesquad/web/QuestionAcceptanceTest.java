package codesquad.web;

import codesquad.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import codesquad.domain.QuestionRepository;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/qna/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        final ResponseEntity<String> response = create(basicAuthTemplate());

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/home"));
    }

    @Test
    public void create_no_login() throws Exception {
        final ResponseEntity<String> response = create(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> create(TestRestTemplate template) throws Exception {
        String title = "질문제목_질문생성";
        final HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", "질문내용_질문생성")
                .build();

        return template.postForEntity("/qna", request, String.class);
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/qna", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains(defaultQuestion().getTitle()), is(true));
        assertThat(response.getBody().contains(defaultQuestion().getWriter().getName()), is(true));
        assertThat(response.getBody().contains(defaultQuestion().getFormattedCreateDate()), is(true));
        assertThat(response.getBody().contains(defaultQuestion().getContents()), is(false));
    }

    @Test
    public void read() {
        final Question question = defaultQuestion();
        final ResponseEntity<String> response = template().getForEntity(String.format("/qna/%d", question.getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains("/questions/" + question.getId() + "/form"), is(true));
        assertThat(response.getBody().contains("/questions/" + question.getId() + "/form"), is(true));
        assertThat(response.getBody().contains(question.getTitle()), is(true));
        assertThat(response.getBody().contains(question.getContents()), is(true));
        assertThat(response.getBody().contains(question.getFormattedCreateDate()), is(true));
    }

    @Test
    public void updateForm_no_login() throws Exception {
        final ResponseEntity<String> response = template().getForEntity(String.format("/qna/%d/form", defaultQuestion().getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update_no_login() throws Exception {
        final ResponseEntity<String> response = update(template());

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() throws Exception {
        final ResponseEntity<String> response = update(basicAuthTemplate());

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/home"));
    }

    private ResponseEntity<String> update(TestRestTemplate template) {
        final HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "put")
                .addParameter("title", "수정된 질문 제목")
                .addParameter("contents", "수정된 질문 내용")
                .build();

        return template.postForEntity(String.format("/qna/%d", defaultQuestion().getId()), request, String.class);
    }
}