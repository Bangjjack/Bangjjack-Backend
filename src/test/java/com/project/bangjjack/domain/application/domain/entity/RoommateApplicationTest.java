package com.project.bangjjack.domain.application.domain.entity;

import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoommateApplicationTest {

    private User author() {
        User user = User.create("provider-author", "작성자", "author@gachon.ac.kr", null);
        user.completeOnboarding(2000, 2, Gender.FEMALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_2);
        return user;
    }

    private User applicant() {
        User user = User.create("provider-applicant", "신청자", "applicant@gachon.ac.kr", null);
        user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        return user;
    }

    private RoommatePost openPost() {
        return RoommatePost.create(author(), "제목", "내용", RoomSize.TWO_PERSON, 1, Semester.SIXTEEN_WEEKS, Dormitory.DORM_2);
    }

    @Test
    @DisplayName("create() 호출 시 status는 PENDING으로 초기화된다")
    void create_시_PENDING_상태로_초기화() {
        RoommatePost post = openPost();
        User applicant = applicant();

        RoommateApplication application = RoommateApplication.create(post, applicant);

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        assertThat(application.getPost()).isSameAs(post);
        assertThat(application.getApplicant()).isSameAs(applicant);
    }

    @Test
    @DisplayName("accept() 호출 시 status가 ACCEPTED로 변경된다")
    void accept_시_상태_변경() {
        RoommateApplication application = RoommateApplication.create(openPost(), applicant());

        application.accept();

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
    }

    @Test
    @DisplayName("reject() 호출 시 status가 REJECTED로 변경된다")
    void reject_시_상태_변경() {
        RoommateApplication application = RoommateApplication.create(openPost(), applicant());

        application.reject();

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }
}
