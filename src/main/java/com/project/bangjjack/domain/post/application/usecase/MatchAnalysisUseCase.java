package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.checklist.domain.service.RoommatePreferenceGetService;
import com.project.bangjjack.domain.post.application.dto.response.MatchRateResponse;
import com.project.bangjjack.domain.post.application.exception.ChecklistNotRegisteredException;
import com.project.bangjjack.domain.post.application.exception.PreferenceNotRegisteredException;
import com.project.bangjjack.domain.post.application.exception.SelfMatchNotAllowedException;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisResult;
import com.project.bangjjack.domain.post.domain.service.FeatureLabelConverter;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchAnalysisUseCase {

    private final UserGetService userGetService;
    private final RoommatePostGetService roommatePostGetService;
    private final ChecklistGetService checklistGetService;
    private final RoommatePreferenceGetService roommatePreferenceGetService;
    private final MatchAnalysisPort matchAnalysisPort;
    private final FeatureLabelConverter featureLabelConverter;

    public MatchRateResponse analyzeMatchRate(Long userId, Long postId) {
        RoommatePost post = roommatePostGetService.getById(postId);
        User author = post.getUser();

        if (author.getId().equals(userId)) {
            throw new SelfMatchNotAllowedException();
        }

        User requester = userGetService.getById(userId);

        MatchAnalysisProfile requesterProfile = buildProfile(requester);
        MatchAnalysisProfile authorProfile = buildProfile(author);

        MatchAnalysisResult result = matchAnalysisPort.analyze(
                MatchAnalysisCommand.of(requesterProfile, authorProfile)
        );

        return MatchRateResponse.of(
                result.matchRate(),
                featureLabelConverter.toLabels(result.matchedFeatures()),
                featureLabelConverter.toLabels(result.topInfluentialFeatures())
        );
    }

    private MatchAnalysisProfile buildProfile(User user) {
        LifestyleChecklist checklist = checklistGetService.findByUser(user)
                .orElseThrow(ChecklistNotRegisteredException::new);
        List<LifestyleChecklistSleepHabit> sleepHabits = checklistGetService.findSleepHabitsByChecklist(checklist);
        RoommatePreference preference = roommatePreferenceGetService.findByUser(user)
                .orElseThrow(PreferenceNotRegisteredException::new);
        return MatchAnalysisProfile.of(user, checklist, sleepHabits, preference);
    }
}
