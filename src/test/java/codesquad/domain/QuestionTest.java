package codesquad.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {

    private static final String TITLE = "질문제목";
    private static final String CONTENTS = "질문본문";
    private Question question;

    private static final long USER_ID = 10;
    private static final String USER_USERID = "유저아이디";
    private static final String USER_PW = "암호";
    private static final String USER_NAME = "이름";
    private static final String USER_MAIL = "메일";
    private User user;

    @Before
    public void setUp() throws Exception {
        question = new Question(TITLE, CONTENTS);
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
}
