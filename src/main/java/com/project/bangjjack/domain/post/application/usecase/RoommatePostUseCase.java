package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.request.CreateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.dto.request.CreateSharedLifestyleRequest;
import com.project.bangjjack.domain.post.application.exception.AlreadyOpenPostExistsException;
import com.project.bangjjack.domain.post.application.exception.InvalidRecruitMemberCountException;
import com.project.bangjjack.domain.post.application.dto.response.RoommatePostDetailResponse;
import com.project.bangjjack.domain.post.application.dto.request.UpdateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.dto.request.UpdateSharedLifestyleRequest;
import com.project.bangjjack.domain.post.application.exception.PostDeleteForbiddenException;
import com.project.bangjjack.domain.post.application.exception.PostNotEditableException;
import com.project.bangjjack.domain.post.application.exception.PostUpdateForbiddenException;
import com.project.bangjjack.domain.post.application.exception.PostWritePreconditionNotMetException;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostCreateService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostDeleteService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostUpdateService;
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
    private final RoommatePostUpdateService roommatePostUpdateService;

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

    public RoommatePostDetailResponse getPostDetail(Long userId, Long postId) {
        RoommatePost post = roommatePostGetService.getById(postId);
        PostSharedLifestyle sharedLifestyle = roommatePostGetService.getSharedLifestyleByPost(post);
        boolean isOwner = post.getUser().getId().equals(userId);
        return RoommatePostDetailResponse.from(post, sharedLifestyle, isOwner);
    }

    @Transactional
    public void updatePost(Long userId, Long postId, UpdateRoommatePostRequest request) {
        User user = userGetService.getById(userId);
        RoommatePost post = roommatePostGetService.getById(postId);

        if (!post.getUser().getId().equals(user.getId())) {
            throw new PostUpdateForbiddenException();
        }

        if (post.getStatus() != PostStatus.OPEN) {
            throw new PostNotEditableException();
        }

        if (!request.roomSize().isValidRecruitCount(request.recruitMemberCount())) {
            throw new InvalidRecruitMemberCountException();
        }

        post.update(request.title(), request.description(), request.roomSize(), request.recruitMemberCount());

        PostSharedLifestyle sharedLifestyle = roommatePostGetService.getSharedLifestyleByPost(post);
        UpdateSharedLifestyleRequest lifestyle = request.sharedLifestyle();
        sharedLifestyle.update(
                lifestyle.roomTrashBinSharing(),
                lifestyle.recycling(),
                lifestyle.phoneCall(),
                lifestyle.itemSharing(),
                lifestyle.earphoneUsage(),
                lifestyle.lightsOutTime()
        );

        roommatePostUpdateService.updatePost(post, sharedLifestyle);
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
