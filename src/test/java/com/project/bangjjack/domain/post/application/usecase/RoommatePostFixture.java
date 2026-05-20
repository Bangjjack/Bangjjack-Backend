package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.post.domain.entity.ItemSharing;
import com.project.bangjjack.domain.post.domain.entity.LightsOutTime;
import com.project.bangjjack.domain.post.domain.entity.PhoneCall;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.Recycling;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

class RoommatePostFixture {

    static User userWithId(Long id) {
        User user = User.create("provider-" + id, "테스트유저" + id, "test" + id + "@gachon.ac.kr", null);
        user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    static RoommatePost postOwnedBy(User owner) {
        return RoommatePost.create(owner, "룸메이트 구해요", "함께 지낼 룸메이트를 찾습니다.",
                RoomSize.TWO_PERSON, 1, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
    }

    static PostSharedLifestyle sharedLifestyleFor(RoommatePost post) {
        return PostSharedLifestyle.create(post, true, Recycling.SHARE_BIN, PhoneCall.SHORT_CALLS_OKAY,
                ItemSharing.NO_PREFERENCE, true, LightsOutTime.BETWEEN_23_24);
    }

    static RoommatePreference preferenceFor(User user) {
        return RoommatePreference.create(user,
                RoommatePreferenceFactor.BEDTIME,
                RoommatePreferenceFactor.CLEANING_HABIT,
                RoommatePreferenceFactor.NOISE_SENSITIVITY);
    }
}
