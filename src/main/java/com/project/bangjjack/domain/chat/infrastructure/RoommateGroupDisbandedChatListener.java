package com.project.bangjjack.domain.chat.infrastructure;

import com.project.bangjjack.domain.roommategroup.application.event.RoommateGroupDisbandedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoommateGroupDisbandedChatListener {

    private final RoommateGroupDisbandedNotifier notifier;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(RoommateGroupDisbandedEvent event) {
        String content = String.format("'%s' 룸메이트 그룹이 해체되어 모집글이 삭제되었습니다.", event.postTitle());

        for (Long memberId : event.memberIds()) {
            try {
                notifier.sendDirectSystemMessage(event.leaderId(), memberId, content);
            } catch (Exception e) {
                log.error("[그룹 해체 알림] 메시지 전송 실패 - leaderId={}, memberId={}", event.leaderId(), memberId, e);
            }
        }
    }
}
