package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.service.RoommatePreferenceGetService;
import com.project.bangjjack.domain.post.application.dto.request.CreateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.dto.request.CreateSharedLifestyleRequest;
import com.project.bangjjack.domain.post.application.exception.AlreadyOpenPostExistsException;
import com.project.bangjjack.domain.post.application.exception.InvalidRecruitMemberCountException;
import com.project.bangjjack.domain.post.application.exception.PostWritePreconditionNotMetException;
import com.project.bangjjack.domain.post.domain.entity.PostPriority;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.repository.RoommatePostRepository;
import com.project.bangjjack.domain.post.domain.service.RoommatePostCreateService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoommatePostUseCase {

    private final UserGetService userGetService;
    private final RoommatePreferenceGetService roommatePreferenceGetService;
    private final RoommatePostRepository roommatePostRepository;
    private final RoommatePostCreateService roommatePostCreateService;

    @Transactional
    public void createPost(Long userId, CreateRoommatePostRequest request) {
        User user = userGetService.getById(userId);

        if (!user.isOnboarded() || !user.isChecklistRegistered() || !user.isRoommatePreferenceRegistered()) {
            throw new PostWritePreconditionNotMetException();
        }

        if (roommatePostRepository.existsByUserAndStatusAndDeletedFalse(user, PostStatus.OPEN)) {
            throw new AlreadyOpenPostExistsException();
        }

        RoommatePreference preference = roommatePreferenceGetService.getByUser(user);

        validateRecruitMemberCount(request.roomSize(), request.recruitMemberCount());

        RoommatePost post = RoommatePost.create(
                user,
                request.title(),
                request.description(),
                request.roomSize(),
                request.recruitMemberCount(),
                user.getSemester(),
                user.getDormitory()
        );

        PostPriority priority = PostPriority.create(
                post,
                preference.getFirstPriority(),
                preference.getSecondPriority(),
                preference.getThirdPriority()
        );

        CreateSharedLifestyleRequest lifestyle = request.sharedLifestyle();
        PostSharedLifestyle sharedLifestyle = PostSharedLifestyle.create(
                post,
                lifestyle.roomTrashBinSharing(),
                lifestyle.recycling(),
                lifestyle.phoneCall(),
                lifestyle.itemSharing(),
                lifestyle.earphoneUsage(),
                lifestyle.lightsOutTime()
        );

        roommatePostCreateService.createPost(post, priority, sharedLifestyle);
    }

    private void validateRecruitMemberCount(RoomSize roomSize, int recruitMemberCount) {
        boolean valid = switch (roomSize) {
            case TWO_PERSON -> recruitMemberCount == 1;
            case THREE_PERSON -> recruitMemberCount >= 1 && recruitMemberCount <= 2;
            case FOUR_PERSON -> recruitMemberCount >= 1 && recruitMemberCount <= 3;
        };
        if (!valid) {
            throw new InvalidRecruitMemberCountException();
        }
    }
}
