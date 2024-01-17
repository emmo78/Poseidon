package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RuleNameService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RuleNameControllerTest {


    @InjectMocks
    private RuleNameController ruleNameController;

    @Mock
    private RuleNameService ruleNameService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @Mock
    private WebRequest request;

    @Mock
    private RequestService requestService;

    @AfterEach
    public void unsetForEachTest() {
        ruleNameService = null;
        ruleNameController = null;
    }

    @Nested
    @Tag("homeRuleNameControllerTests")
    @DisplayName("Tests for /ruleName/list")
    class HomeRuleNameControllerTests {
        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test home should return \"ruleName/list\"")
        public void homeTestShouldReturnRuleNameList() {

            //GIVEN
            when(ruleNameService.getRuleNames(any(Pageable.class))).thenReturn(new PageImpl<RuleName>(new ArrayList<>()));

            //WHEN
            String html = ruleNameController.home(model, request);

            //THEN
            assertThat(html).isEqualTo("ruleName/list");
        }

        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test home should throw UnexpectedRollbackException")
        public void homeTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(ruleNameService.getRuleNames(any(Pageable.class))).thenThrow(new UnexpectedRollbackException("Error while getting ruleNamess"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> ruleNameController.home(model, request))
                    .getMessage()).isEqualTo("Error while getting ruleNames");
        }
    }

    @Test
    @Tag("RuleNameControllerTest")
    @DisplayName("test addRuleNameForm should return \"ruleName/add\"")
    public void ruleNameAddTestShouldReturnStringRuleNameAdd() {

        //GIVEN
        RuleName ruleName = new RuleName();

        //WHEN
        String html = ruleNameController.addRuleNameForm(ruleName);

        //THEN
        assertThat(html).isEqualTo("ruleName/add");
    }

    @Nested
    @Tag("validateRuleNameControllerTests")
    @DisplayName("Tests for /ruleName/validate")
    class ValidateRuleNameControllerTests {

        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test validate should return \"redirect:/ruleName/list\"")
        public void validateTestShouldReturnStringRedirectRuleNameList() {

            //GIVEN
            RuleName ruleName = new RuleName();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(ruleNameService.saveRuleName(any(RuleName.class))).thenReturn(ruleName);

            //WHEN
            String html = ruleNameController.validate(ruleName, bindingResult, request);

            //THEN
            assertThat(html).isEqualTo("redirect:/ruleName/list");
        }

        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test validate should return \"ruleName/add\" on BindingResultError")
        public void validateTestShouldReturnStringRuleNameAddOnBindingResulError() {

            //GIVEN
            RuleName ruleName = new RuleName();
            when(bindingResult.hasErrors()).thenReturn(true);

            //WHEN
            String html = ruleNameController.validate(ruleName, bindingResult, request);

            //THEN
            assertThat(html).isEqualTo("ruleName/add");
        }

        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test validate should throw UnexpectedRollbackException")
        public void validateTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            RuleName ruleName = new RuleName();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(ruleNameService.saveRuleName(any(RuleName.class))).thenThrow(new UnexpectedRollbackException("Error while saving ruleName"));
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> ruleNameController.validate(ruleName, bindingResult, request))
                    .getMessage()).isEqualTo("Error while saving ruleName");
        }
    }

    @Nested
    @Tag("showUpdateFormRuleNameControllerTests")
    @DisplayName("Tests for /ruleName/update/{id}")
    class ShowUpdateFormRuleNameControllerTests {

        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test showUpdateForm should return \"ruleName/update\"")
        public void showUpdateFormTestShouldReturnStringRuleNameUpdate() {

            //GIVEN
            RuleName ruleName = new RuleName();
            when(ruleNameService.getRuleNameById(anyInt())).thenReturn(ruleName);

            //WHEN
            String html = ruleNameController.showUpdateForm(1, model, request);

            //THEN
            assertThat(html).isEqualTo("ruleName/update");
        }

        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test showUpdateForm should throw UnexpectedRollbackException")
        public void showUpdateFormTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(ruleNameService.getRuleNameById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting ruleName"));
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> ruleNameController.showUpdateForm(1, model, request))
                    .getMessage()).isEqualTo("Error while getting ruleName");
        }
    }

    @Nested
    @Tag("updateBidRuleNameControllerTests")
    @DisplayName("Tests for /ruleName/update")
    class updateBidRuleNameControllerTests {

        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test update RuleName should return \"redirect:/ruleName/list\"")
        public void updateCurveTestShouldReturnStringRedirectRuleNameList() {

            //GIVEN
            RuleName ruleName = new RuleName();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(ruleNameService.saveRuleName(any(RuleName.class))).thenReturn(ruleName);

            //WHEN
            String html = ruleNameController.updateRuleName(ruleName, bindingResult, request);

            //THEN
            assertThat(html).isEqualTo("redirect:/ruleName/list");
        }

        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test update RuleName should return \"ruleName/update\" on BindingResultError")
        public void updateCurveTestShouldReturnStringRuleNameUpdateOnBindingResulError() {

            //GIVEN
            RuleName ruleName = new RuleName();
            when(bindingResult.hasErrors()).thenReturn(true);

            //WHEN
            String html = ruleNameController.updateRuleName(ruleName, bindingResult, request);

            //THEN
            assertThat(html).isEqualTo("ruleName/update");
        }

        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test update Bid should throw UnexpectedRollbackException")
        public void updateBidTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            RuleName ruleName = new RuleName();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(ruleNameService.saveRuleName(any(RuleName.class))).thenThrow(new UnexpectedRollbackException("Error while saving ruleName"));
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> ruleNameController.updateRuleName(ruleName, bindingResult, request))
                    .getMessage()).isEqualTo("Error while saving ruleName");
        }
    }

    @Nested
    @Tag("deleteBidRuleNameControllerTests")
    @DisplayName("Tests for /ruleName/delete/{id}")
    class deleteBidRuleNameControllerTests {

        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test delete RuleName should return \"redirect:/ruleName/list\"")
        public void deleteCurveTestShouldReturnStringRedirectCurvePointList() {

            //GIVEN
            //WHEN
            String html = ruleNameController.deleteRuleName(1, request);

            //THEN
            assertThat(html).isEqualTo("redirect:/ruleName/list");
        }

        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test delete Bid should throw UnexpectedRollbackException")
        public void deleteBidTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            doThrow(new UnexpectedRollbackException("Error while deleting ruleName")).when(ruleNameService).deleteRuleNameById(anyInt());
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> ruleNameController.deleteRuleName(1, request))
                    .getMessage()).isEqualTo("Error while deleting ruleName");
        }
    }

}
