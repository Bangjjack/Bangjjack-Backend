package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.request.CreateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.dto.request.SharedLifestyleRequest;
import com.project.bangjjack.domain.post.application.dto.response.RoommatePostSummaryResponse;
import com.project.bangjjack.domain.post.application.exception.AlreadyOpenPostExistsException;
import com.project.bangjjack.domain.post.application.exception.InvalidRecruitMemberCountException;
import com.project.bangjjack.domain.post.application.dto.response.RoommatePostDetailResponse;
import com.project.bangjjack.domain.post.application.dto.request.UpdateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.exception.PostDeleteForbiddenException;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.global.common.response.SliceResponse;
import org.springframework.data.domain.Pageable;
import com.project.bangjjack.domain.post.application.exception.PostNotEditableException;
import com.project.bangjjack.domain.post.application.exception.PostUpdateForbiddenException;
import com.project.bangjjack.domain.post.application.exception.PostWritePreconditionNotMetException;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostCreateService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostDeleteService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostUpdateService;
import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupCreateService;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberCreateService;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberGetService;

import java.util.List;
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
    private final RoommateGroupCreateService roommateGroupCreateService;
    private final RoommateGroupMemberCreateService roommateGroupMemberCreateService;
    private final RoommateGroupMemberGetService roommateGroupMemberGetService;

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

        SharedLifestyleRequest lifestyle = request.sharedLifestyle();
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

        RoommateGroup group = roommateGroupCreateService.createGroup(post);
        roommateGroupMemberCreateService.addMember(group, user, GroupMemberRole.LEADER);
    }

    public SliceResponse<RoommatePostSummaryResponse> getPostList(Campus campus, RoomSize roomSize, Pageable pageable) {
        return SliceResponse.from(
                roommatePostGetService.getPostList(campus, roomSize, pageable)
                        .map(RoommatePostSummaryResponse::from)
        );
    }

    public RoommatePostDetailResponse getPostDetail(Long userId, Long postId) {
        PostSharedLifestyle sharedLifestyle = roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(postId);
        RoommatePost post = sharedLifestyle.getPost();
        boolean isOwner = post.getUser().getId().equals(userId);
        List<RoommateGroupMember> members = roommateGroupMemberGetService.getActiveMembersWithUserByPostId(postId);
        return RoommatePostDetailResponse.from(post, sharedLifestyle, isOwner, members);
    }

    @Transactional
    public void updatePost(Long userId, Long postId, UpdateRoommatePostRequest request) {
        RoommatePost post = roommatePostGetService.getById(postId);

        if (!post.getUser().getId().equals(userId)) {
            throw new PostUpdateForbiddenException();
        }

        if (!post.isEditable()) {
            throw new PostNotEditableException();
        }

        if (!request.roomSize().isValidRecruitCount(request.recruitMemberCount())) {
            throw new InvalidRecruitMemberCountException();
        }

        PostSharedLifestyle sharedLifestyle = roommatePostGetService.getSharedLifestyleByPost(post);
        SharedLifestyleRequest lifestyle = request.sharedLifestyle();
        roommatePostUpdateService.updatePost(
                post, sharedLifestyle,
                request.title(), request.description(), request.roomSize(), request.recruitMemberCount(),
                lifestyle.roomTrashBinSharing(), lifestyle.recycling(), lifestyle.phoneCall(),
                lifestyle.itemSharing(), lifestyle.earphoneUsage(), lifestyle.lightsOutTime()
        );
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        RoommatePost post = roommatePostGetService.getById(postId);

        if (!post.getUser().getId().equals(userId)) {
            throw new PostDeleteForbiddenException();
        }

        PostSharedLifestyle sharedLifestyle = roommatePostGetService.getSharedLifestyleByPost(post);
        roommatePostDeleteService.deletePost(post, sharedLifestyle);
    }
}
