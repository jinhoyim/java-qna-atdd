package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {

    private long QUESTION_ID = 10L;
    private String TITLE = "질문제목";
    private String CONTENTS = "질문본문";
    private Question question;

    private long USER_ID = 10;
    private String USER_USERID = "유저아이디";
    private String USER_PW = "암호";
    private String USER_NAME = "이름";
    private String USER_MAIL = "메일";
    private User user;

    @Before
    public void setUp() throws Exception {
        question = new Question(QUESTION_ID, TITLE, CONTENTS);
        user = new User(USER_ID, USER_USERID, USER_PW, USER_NAME, USER_MAIL);
    }

    @Test
    public void 제목조회() {
        final String title = question.getTitle();
        assertThat(title).isEqualTo(TITLE);
    }

    @Test
    public void 내용조회() {
        String contents = question.getContents();
        assertThat(contents).isEqualTo(CONTENTS);
    }

    @Test
    public void 작성자() {
        question.writeBy(user);
        final User writer = question.getWriter();
        assertThat(user).isEqualTo(writer);
    }

    @Test
    public void 소유자체크() {
        question.writeBy(user);
        final boolean isOwner = question.isOwner(user);
        assertThat(isOwner).isTrue();
    }

    @Test
    public void 소유자아님() {
        question.writeBy(user);
        User otherUser = new User(1000, "aa", "bb", "cc", "dd");
        final boolean isOwner = question.isOwner(otherUser);
        assertThat(isOwner).isFalse();
    }

    @Test
    public void update_owner() {
        question.writeBy(user);

        String updateTitle = "수정된질문제목";
        String updateContent = "수정된질문내용";
        Question target = new Question(updateTitle, updateContent);

        question.update(user, target);

        assertThat(question.getTitle()).isEqualTo(updateTitle);
        assertThat(question.getContents()).isEqualTo(updateContent);
        assertThat(question.getWriter()).isEqualTo(user);
    }

    @Test
    public void update_return() {
        question.writeBy(user);

        String updateTitle = "수정된질문제목";
        String updateContent = "수정된질문내용";
        Question target = new Question(updateTitle, updateContent);

        final Question update = question.update(user, target);

        assertThat(question).isEqualTo(update);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        question.writeBy(user);
        User otherUser = new User(100, "other123", "pw11", "otherName", "email");

        String updateTitle = "수정된질문제목";
        String updateContent = "수정된질문내용";
        Question target = new Question(updateTitle, updateContent);

        question.update(otherUser, target);
    }
}
