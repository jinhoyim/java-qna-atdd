package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class QuestionTest {
    public static final Question FIRST_QUESTION;
    public static final Question SECOND_QUESTION;

    public static final String UPDATE_TITLE = "수정제목";
    public static final String UPDATE_CONTENT = "수정본문";
    public static final Question UPDATE_QUESTION = new Question(UPDATE_TITLE, UPDATE_CONTENT);

    public static Question originQuestion() {
        final Question question = new Question(FIRST_QUESTION.getId(), FIRST_QUESTION.getTitle(), FIRST_QUESTION.getContents());
        question.writeBy(FIRST_QUESTION.getWriter());
        return question;
    }

    static {
        FIRST_QUESTION = new Question(1, "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다. Play 프레임워크는 정말 한 순간 잠시 눈에 뜨이다가 사라져 버렸다. RoR과 Play 기반으로 개발을 해보면 정말 생산성이 높으며, 웹 프로그래밍이 재미있기까지 하다. Spring MVC + JPA(Hibernate) 기반으로 진행하면 설정할 부분도 많고, 기본으로 지원하지 않는 기능도 많아 RoR과 Play에서 기본적으로 지원하는 기능을 서비스하려면 추가적인 개발이 필요하다.");
        FIRST_QUESTION.writeBy(UserTest.JAVAJIGI);

        SECOND_QUESTION = new Question(2, "runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?", "설계를 희한하게 하는 바람에 꼬인 문제같긴 합니다만. 여쭙습니다. 상황은 mybatis select 실행될 시에 return object 의 getter 가 호출되면서인데요. getter 안에 다른 property 에 의존중인 코드가 삽입되어 있어서, 만약 다른 mybatis select 구문에 해당 property 가 없다면 exception 이 발생하게 됩니다.");
        SECOND_QUESTION.writeBy(UserTest.SANJIGI);
    }

    @Test
    public void 제목조회() {
        final String title = FIRST_QUESTION.getTitle();
        assertThat(title).isEqualTo("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?");
    }

    @Test
    public void 내용조회() {
        String contents = FIRST_QUESTION.getContents();
        assertThat(contents).isEqualTo("Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다. Play 프레임워크는 정말 한 순간 잠시 눈에 뜨이다가 사라져 버렸다. RoR과 Play 기반으로 개발을 해보면 정말 생산성이 높으며, 웹 프로그래밍이 재미있기까지 하다. Spring MVC + JPA(Hibernate) 기반으로 진행하면 설정할 부분도 많고, 기본으로 지원하지 않는 기능도 많아 RoR과 Play에서 기본적으로 지원하는 기능을 서비스하려면 추가적인 개발이 필요하다.");
    }

    @Test
    public void 작성자() {
        final User writer = FIRST_QUESTION.getWriter();
        assertThat(writer).isEqualTo(UserTest.JAVAJIGI);
    }

    @Test
    public void 소유자체크() {
        final boolean isOwner = FIRST_QUESTION.isOwner(UserTest.JAVAJIGI);
        assertThat(isOwner).isTrue();
    }

    @Test
    public void 소유자아님() {
        User otherUser = UserTest.SANJIGI;
        final boolean isOwner = FIRST_QUESTION.isOwner(otherUser);
        assertThat(isOwner).isFalse();
    }

    @Test
    public void update_by_owner() {
        Question origin = originQuestion();

        origin.update(UserTest.JAVAJIGI, UPDATE_QUESTION);

        assertThat(origin.getTitle()).isEqualTo(UPDATE_TITLE);
        assertThat(origin.getContents()).isEqualTo(UPDATE_CONTENT);
        assertThat(origin.getWriter()).isEqualTo(UserTest.JAVAJIGI);
    }

    @Test
    public void update_return() {
        Question origin = originQuestion();

        final Question update = origin.update(UserTest.JAVAJIGI, UPDATE_QUESTION);

        assertThat(update).isEqualTo(FIRST_QUESTION);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_by_not_owner() {
        Question origin = originQuestion();

        origin.update(UserTest.SANJIGI, UPDATE_QUESTION);
    }

    @Test
    public void delete_by_owner() {
        Question origin = originQuestion();
        origin.delete(UserTest.JAVAJIGI);
        assertThat(origin.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_by_not_owner() {
        Question origin = originQuestion();
        origin.delete(UserTest.SANJIGI);
    }
}

