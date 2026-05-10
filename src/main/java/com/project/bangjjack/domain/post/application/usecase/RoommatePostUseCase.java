package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.request.CreateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.dto.request.CreateSharedLifestyleRequest;
import com.project.bangjjack.domain.post.application.exception.AlreadyOpenPostExistsException;
import com.project.bangjjack.domain.post.application.exception.InvalidRecruitMemberCountException;
import com.project.bangjjack.domain.post.application.exception.PostDeleteForbiddenException;
import com.project.bangjjack.domain.post.application.exception.PostWritePreconditionNotMetException;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostCreateService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostDeleteService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
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
    private final RoommatePostGetService roommatePostGetService;
    private final RoommatePostCreateService roommatePostCreateService;
    private final RoommatePostDeleteService roommatePostDeleteService;

    @Transactional
    public void createPost(Long userId, CreateRoommatePostRequest request) {
        User user = userGetService.getById(userId);

        if (!user.isOnboarded() || !user.isChecklistRegistered() || !user.isRoommatePreferenceRegistered()) {
            throw new PostWritePreconditionNotMetException();
        }

        if (roommatePostGetService.existsOpenPostByUser(user)) {
            throw new AlreadyOpenPostExistsException();
        }

        if (!request.roomSize().isValidRecruitCount(request.recruitMemberCount())) {
            throw new InvalidRecruitMemberCountException();
        }

        RoommatePost post = RoommatePost.create(
                user,
                request.title(),
                request.description(),
                request.roomSize(),
                request.recruitMemberCount(),
                user.getSemester(),
                user.getDormitory()
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

        roommatePostCreateService.createPost(post, sharedLifestyle);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        User user = userGetService.getById(userId);
        RoommatePost post = roommatePostGetService.getById(postId);

        if (!post.getUser().getId().equals(user.getId())) {
            throw new PostDeleteForbiddenException();
        }

        roommatePostDeleteService.deletePost(post);
    }
}
