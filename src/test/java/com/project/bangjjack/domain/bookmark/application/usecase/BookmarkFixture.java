package com.project.bangjjack.domain.bookmark.application.usecase;

import com.project.bangjjack.domain.bookmark.domain.entity.PostBookmark;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

class BookmarkFixture {

    static User userWithId(Long id) {
        User user = User.create("provider-" + id, "테스트유저" + id, "test" + id + "@gachon.ac.kr", null);
        user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    static RoommatePost postWithId(Long id, User owner) {
        RoommatePost post = RoommatePost.create(owner, "룸메이트 구해요", "함께 지낼 룸메이트를 찾습니다.",
                RoomSize.TWO_PERSON, 1, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }

    static PostBookmark activeBookmarkWithId(Long id, User user, RoommatePost post) {
        PostBookmark bookmark = PostBookmark.create(user, post);
        ReflectionTestUtils.setField(bookmark, "id", id);
        return bookmark;
    }

    static PostBookmark inactiveBookmarkWithId(Long id, User user, RoommatePost post) {
        PostBookmark bookmark = PostBookmark.create(user, post);
        bookmark.deactivate();
        ReflectionTestUtils.setField(bookmark, "id", id);
        return bookmark;
    }
}
