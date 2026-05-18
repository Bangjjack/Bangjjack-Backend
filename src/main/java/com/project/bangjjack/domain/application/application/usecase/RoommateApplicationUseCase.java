package com.project.bangjjack.domain.application.application.usecase;

import com.project.bangjjack.domain.application.application.dto.response.CreateRoommateApplicationResponse;
import com.project.bangjjack.domain.application.application.exception.AlreadyAppliedPendingException;
import com.project.bangjjack.domain.application.application.exception.ApplicationPreconditionNotMetException;
import com.project.bangjjack.domain.application.application.exception.CannotApplyToOwnPostException;
import com.project.bangjjack.domain.application.application.exception.OwnOpenPostExistsForApplicantException;
import com.project.bangjjack.domain.application.application.exception.PostNotOpenException;
import com.project.bangjjack.domain.application.domain.entity.RoommateApplication;
import com.project.bangjjack.domain.application.domain.service.RoommateApplicationCreateService;
import com.project.bangjjack.domain.application.domain.service.RoommateApplicationGetService;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomCreateService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoommateApplicationUseCase {

    private final UserGetService userGetService;
    private final RoommatePostGetService roommatePostGetService;
    private final RoommateApplicationGetService roommateApplicationGetService;
    private final RoommateApplicationCreateService roommateApplicationCreateService;
    private final ChatRoomGetService chatRoomGetService;
    private final ChatRoomCreateService chatRoomCreateService;

    @Transactional
    public CreateRoommateApplicationResponse createApplication(Long userId, Long postId) {
        User applicant = userGetService.getById(userId);

        if (!applicant.isOnboarded() || !applicant.isChecklistRegistered() || !applicant.isRoommatePreferenceRegistered()) {
            throw new ApplicationPreconditionNotMetException();
        }

        RoommatePost post = roommatePostGetService.getById(postId);

        Long authorId = post.getUser().getId();
        if (authorId.equals(userId)) {
            throw new CannotApplyToOwnPostException();
        }

        if (post.getStatus() != PostStatus.OPEN) {
            throw new PostNotOpenException();
        }

        if (roommatePostGetService.existsOpenPostByUser(applicant)) {
            throw new OwnOpenPostExistsForApplicantException();
        }

        if (roommateApplicationGetService.existsPendingByPostIdAndApplicantId(postId, userId)) {
            throw new AlreadyAppliedPendingException();
        }

        String directRoomKey = chatRoomCreateService.createDirectKey(userId, authorId);
        Optional<ChatRoom> existing = chatRoomGetService.findByDirectRoomKey(directRoomKey);
        boolean isNewChatRoom = existing.isEmpty();
        ChatRoom chatRoom = existing.orElseGet(
                () -> chatRoomCreateService.createDirectRoom(userId, authorId, directRoomKey));

        RoommateApplication saved = roommateApplicationCreateService.createApplication(
                RoommateApplication.create(post, applicant)
        );

        return CreateRoommateApplicationResponse.from(saved, chatRoom.getId(), isNewChatRoom);
    }
}
