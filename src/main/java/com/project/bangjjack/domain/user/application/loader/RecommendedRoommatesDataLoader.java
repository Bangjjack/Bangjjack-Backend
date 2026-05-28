package com.project.bangjjack.domain.user.application.loader;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistBundle;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.checklist.domain.service.RoommatePreferenceGetService;
import com.project.bangjjack.domain.post.application.exception.ChecklistNotRegisteredException;
import com.project.bangjjack.domain.post.application.exception.PreferenceNotRegisteredException;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;
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
public class RecommendedRoommatesDataLoader {

    private final UserGetService userGetService;
    private final ChecklistGetService checklistGetService;
    private final RoommatePreferenceGetService roommatePreferenceGetService;

    @Transactional(readOnly = true)
    public RecommendedRoommatesBundle loadBundle(Long userId) {
        User requester = userGetService.getById(userId);
        MatchAnalysisProfile requesterProfile = buildRequesterProfile(requester);

        List<User> candidateUsers = userGetService.findCandidatesForRecommendation(requester);
        if (candidateUsers.isEmpty()) {
            return RecommendedRoommatesBundle.of(requesterProfile, List.of());
        }

        Set<Long> candidateIds = candidateUsers.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        Map<Long, ChecklistBundle> bundlesByUserId = checklistGetService.getChecklistBundlesByUserIds(candidateIds);
        Map<Long, RoommatePreference> preferencesByUserId = roommatePreferenceGetService.getByUserIdIn(candidateIds);

        List<CandidateUserEntry> candidates = candidateUsers.stream()
                .map(user -> buildEntry(user, bundlesByUserId, preferencesByUserId))
                .filter(entry -> entry != null)
                .toList();

        return RecommendedRoommatesBundle.of(requesterProfile, candidates);
    }

    private MatchAnalysisProfile buildRequesterProfile(User user) {
        LifestyleChecklist checklist = checklistGetService.findByUser(user)
                .orElseThrow(ChecklistNotRegisteredException::new);
        List<LifestyleChecklistSleepHabit> sleepHabits = checklistGetService.findSleepHabitsByChecklist(checklist);
        RoommatePreference preference = roommatePreferenceGetService.findByUser(user)
                .orElseThrow(PreferenceNotRegisteredException::new);
        return MatchAnalysisProfile.of(user, checklist, sleepHabits, preference);
    }

    private CandidateUserEntry buildEntry(
            User user,
            Map<Long, ChecklistBundle> bundlesByUserId,
            Map<Long, RoommatePreference> preferencesByUserId
    ) {
        ChecklistBundle bundle = bundlesByUserId.get(user.getId());
        if (bundle == null) {
            return null;
        }
        RoommatePreference preference = preferencesByUserId.get(user.getId());
        if (preference == null) {
            return null;
        }
        MatchAnalysisProfile profile = MatchAnalysisProfile.of(
                user,
                bundle.checklist(),
                bundle.sleepHabits(),
                preference
        );
        return CandidateUserEntry.of(user, profile);
    }
}
