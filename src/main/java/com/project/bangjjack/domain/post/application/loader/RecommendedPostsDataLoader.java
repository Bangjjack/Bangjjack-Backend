package com.project.bangjjack.domain.post.application.loader;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistBundle;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.checklist.domain.service.RoommatePreferenceGetService;
import com.project.bangjjack.domain.post.application.exception.ChecklistNotRegisteredException;
import com.project.bangjjack.domain.post.application.exception.PreferenceNotRegisteredException;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberGetService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendedPostsDataLoader {

    private final UserGetService userGetService;
    private final ChecklistGetService checklistGetService;
    private final RoommatePreferenceGetService roommatePreferenceGetService;
    private final RoommatePostGetService roommatePostGetService;
    private final RoommateGroupMemberGetService roommateGroupMemberGetService;

    @Transactional(readOnly = true)
    public RecommendedPostsBundle loadBundle(Long userId, RoomSize roomSize) {
        User requester = userGetService.getById(userId);
        MatchAnalysisProfile requesterProfile = buildRequesterProfile(requester);

        List<RoommatePost> posts = roommatePostGetService.getRecommendationCandidates(requester, roomSize);
        if (posts.isEmpty()) {
            return RecommendedPostsBundle.of(requesterProfile, List.of());
        }

        Set<Long> authorIds = posts.stream()
                .map(post -> post.getUser().getId())
                .collect(Collectors.toSet());
        Set<Long> postIds = posts.stream()
                .map(RoommatePost::getId)
                .collect(Collectors.toSet());

        Map<Long, ChecklistBundle> bundlesByUserId = checklistGetService.getChecklistBundlesByUserIds(authorIds);
        Map<Long, RoommatePreference> preferencesByUserId = roommatePreferenceGetService.getByUserIdIn(authorIds);
        Map<Long, Long> memberCountByPostId = roommateGroupMemberGetService.countActiveByPostIdIn(postIds);

        List<CandidateEntry> candidates = posts.stream()
                .map(post -> buildEntry(post, bundlesByUserId, preferencesByUserId, memberCountByPostId))
                .filter(entry -> entry != null)
                .toList();

        return RecommendedPostsBundle.of(requesterProfile, candidates);
    }

    private MatchAnalysisProfile buildRequesterProfile(User user) {
        LifestyleChecklist checklist = checklistGetService.findByUser(user)
                .orElseThrow(ChecklistNotRegisteredException::new);
        List<LifestyleChecklistSleepHabit> sleepHabits = checklistGetService.findSleepHabitsByChecklist(checklist);
        RoommatePreference preference = roommatePreferenceGetService.findByUser(user)
                .orElseThrow(PreferenceNotRegisteredException::new);
        return MatchAnalysisProfile.of(user, checklist, sleepHabits, preference);
    }

    private CandidateEntry buildEntry(
            RoommatePost post,
            Map<Long, ChecklistBundle> bundlesByUserId,
            Map<Long, RoommatePreference> preferencesByUserId,
            Map<Long, Long> memberCountByPostId
    ) {
        Long authorId = post.getUser().getId();
        ChecklistBundle bundle = bundlesByUserId.get(authorId);
        if (bundle == null) {
            return null;
        }
        RoommatePreference preference = preferencesByUserId.get(authorId);
        if (preference == null) {
            return null;
        }
        int currentMemberCount = memberCountByPostId.getOrDefault(post.getId(), 0L).intValue();
        MatchAnalysisProfile profile = MatchAnalysisProfile.of(
                post.getUser(),
                bundle.checklist(),
                bundle.sleepHabits(),
                preference
        );
        return CandidateEntry.of(post, profile, currentMemberCount);
    }
}
