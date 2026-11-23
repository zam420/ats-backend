package com.deepconnect.ats.evaluationplan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.deepconnect.ats.common.dto.EnumsExtendedRes;
import com.deepconnect.ats.common.service.EnumsResService;
import com.deepconnect.ats.common.service.LocalizationService;
import com.deepconnect.ats.evaluationplan.repo.EvaluationPlanRepository;
import com.deepconnect.ats.evaluationplan.repo.EvaluationPlanRoundRepository;
import com.deepconnect.ats.evaluationplan.repo.EvaluationPlanShareRepository;
import com.deepconnect.ats.user.dto.UserSummary;
import com.deepconnect.ats.user.service.UserServiceV2;

class EvaluationPlanFiltersTest {
    @Test
    void filtersContainEvaluationRoundTypesAndActiveUsers() {
        UserServiceV2 users = mock(UserServiceV2.class);
        UserSummary user = UserSummary.builder().id("user1").name("User").email("user@example.com").build();
        when(users.findActiveUsersForTenant()).thenReturn(List.of(user));
        EvaluationPlanService service = new EvaluationPlanService(
                new EnumsResService(mock(LocalizationService.class)), users,
                mock(EvaluationPlanAccessService.class), mock(EvaluationPlanRepository.class),
                mock(EvaluationPlanRoundRepository.class), mock(EvaluationPlanShareRepository.class));

        var filters = service.getFilters();

        assertEquals(List.of("HR", "FUNCTIONAL", "EXECUTIVE", "CUSTOM"),
                filters.getControlTypes().stream().map(EnumsExtendedRes::getCode).toList());
        assertEquals(List.of(user), filters.getUsers());
    }
}
