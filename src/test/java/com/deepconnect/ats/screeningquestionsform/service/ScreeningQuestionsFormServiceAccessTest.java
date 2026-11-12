package com.deepconnect.ats.screeningquestionsform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.test.util.ReflectionTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestion;
import com.deepconnect.ats.screeningquestionsform.repo.ScreeningQuestionRepository;
import com.deepconnect.ats.screeningquestionsform.repo.ScreeningQuestionTypeRepository;
import com.deepconnect.ats.screeningquestionsform.repo.ScreeningQuestionsFormRepository;
import com.deepconnect.ats.screeningquestionsform.repo.ScreeningQuestionsFormShareRepository;
import com.deepconnect.ats.user.service.UserServiceV2;

class ScreeningQuestionsFormServiceAccessTest {
    private final ScreeningQuestionsFormRepository repository = mock(ScreeningQuestionsFormRepository.class);
    private final ScreeningQuestionsFormService service = new ScreeningQuestionsFormService(
            mock(UserServiceV2.class), new ScreeningQuestionsFormAccessService(), repository,
            mock(ScreeningQuestionRepository.class), mock(ScreeningQuestionTypeRepository.class),
            mock(ScreeningQuestionsFormShareRepository.class));
    private final LoggedInUserDetails user = new LoggedInUserDetails(
            1000L, 2000L, "user", "user@example.test", false, false, List.of());

    @Test
    void sharedAndAllQueriesUseCurrentTenantAndUser() {
        when(repository.findPublicOrSharedForTenant(Mockito.eq(1000L), Mockito.eq(2000L), any())).thenReturn(Page.empty());
        when(repository.findAllForTenantNonAdmin(Mockito.eq(1000L), Mockito.eq(2000L), any())).thenReturn(Page.empty());
        when(repository.findAllForTenantNonAdminAsList(1000L, 2000L)).thenReturn(List.of());
        try (MockedStatic<AuthService> auth = Mockito.mockStatic(AuthService.class)) {
            auth.when(AuthService::getLoggedInUser).thenReturn(user);
            auth.when(AuthService::getLoggedInTenantId).thenReturn(1000L);
            service.findAllSharedWithUser(new AtsPageQuery());
            service.findAll(new AtsPageQuery());
            service.findAllAsList();
        }
        verify(repository).findPublicOrSharedForTenant(Mockito.eq(1000L), Mockito.eq(2000L), any());
        verify(repository).findAllForTenantNonAdmin(Mockito.eq(1000L), Mockito.eq(2000L), any());
        verify(repository).findAllForTenantNonAdminAsList(1000L, 2000L);
    }

    @Test
    void crossTenantSystemTemplateCanBeLoaded() {
        ScreeningQuestionsForm form = new ScreeningQuestionsForm();
        form.setPublicId("system-form");
        form.setTenantId(3000L);
        form.setOwnerId(4000L);
        form.setIsPublicForAll(true);
        when(repository.findByDeactivatedFalseAndPublicId("system-form")).thenReturn(Optional.of(form));
        try (MockedStatic<AuthService> auth = Mockito.mockStatic(AuthService.class)) {
            auth.when(AuthService::getLoggedInUser).thenReturn(user);
            assertEquals("system-form", service.findByPublicIdForTenant("system-form").getId());
        }
    }

    @Test
    void requestQuestionFieldsUseSnakeCase() throws Exception {
        ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        ScreeningQuestion question = mapper.readValue(
                "{\"screening_question_type_id\":3,\"label\":\"Name\",\"default_option\":\"A\","
                + "\"is_mandatory\":true,\"is_drop_down_multi_select\":true,\"max_chars\":40}",
                ScreeningQuestion.class);
        assertEquals(3, question.getScreeningQuestionTypeId());
        assertEquals("A", question.getDefaultOption());
        assertEquals(true, question.getIsMandatory());
        assertEquals(true, question.getIsDropDownMultiSelect());
        assertEquals(40, question.getMaxChars());
    }

    @Test
    void changedDefaultOptionMakesQuestionsUnequal() {
        ScreeningQuestion existing = new ScreeningQuestion();
        existing.setDefaultOption("A");
        ScreeningQuestion incoming = new ScreeningQuestion();
        incoming.setDefaultOption("A");
        assertEquals(true, ReflectionTestUtils.invokeMethod(service, "questionsEqual", List.of(existing), List.of(incoming)));
        incoming.setDefaultOption("B");
        assertEquals(false, ReflectionTestUtils.invokeMethod(service, "questionsEqual", List.of(existing), List.of(incoming)));
        incoming.setDefaultOption(null);
        assertEquals(false, ReflectionTestUtils.invokeMethod(service, "questionsEqual", List.of(existing), List.of(incoming)));
    }
}
