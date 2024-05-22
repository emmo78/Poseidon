package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.Rating;
import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RequestServiceImpl;
import com.poseidoninc.poseidon.service.RuleNameService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * unit test class for the RuleNameController.
 * @author olivier morel
 */
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

    @Spy
    private final RequestService requestService = new RequestServiceImpl();

    private MockHttpServletRequest requestMock;
    private WebRequest request;

    @AfterEach
    public void unsetForEachTest() {
        ruleNameService = null;
        ruleNameController = null;
    }

    @Nested
    @Tag("homeRuleNameControllerTests")
    @DisplayName("Tests for /ruleName/list")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class HomeRuleNameControllerTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/ruleName/list");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }
        
        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test home should return \"ruleName/list\"")
        public void homeTestShouldReturnStringRuleNameList() {

            //GIVEN
            List<RuleName> expectedRuleNames = new ArrayList<>();
            RuleName ruleName = new RuleName();
            ruleName.setId(1);
            ruleName.setName("Rule Name");
            ruleName.setDescription("Rule Description");
            ruleName.setJson("Json");
            ruleName.setTemplate("Template");
            ruleName.setSqlStr("SQL");
            ruleName.setSqlPart("SQL Part");
            expectedRuleNames.add(ruleName);

            RuleName ruleName2 = new RuleName();
            ruleName2.setId(2);
            ruleName2.setName("Rule Name2");
            ruleName2.setDescription("Rule Description2");
            ruleName2.setJson("Json2");
            ruleName2.setTemplate("Template2");
            ruleName2.setSqlStr("SQL2");
            ruleName2.setSqlPart("SQL Part2");
            expectedRuleNames.add(ruleName2);

            ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Iterable<RuleName>> iterableArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);
            when(ruleNameService.getRuleNames(any(Pageable.class))).thenReturn(new PageImpl<RuleName>(expectedRuleNames));

            //WHEN
            String html = ruleNameController.home(model, request);

            //THEN
            verify(model).addAttribute(stringArgumentCaptor.capture(), iterableArgumentCaptor.capture()); //times(1) is used by default
            assertThat(stringArgumentCaptor.getValue()).isEqualTo("ruleNames");
            assertThat(iterableArgumentCaptor.getValue()).containsExactlyElementsOf(expectedRuleNames);
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
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ValidateRuleNameControllerTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/ruleName/validate/");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

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
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ShowUpdateFormRuleNameControllerTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/ruleName/update/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("RuleNameControllerTest")
        @DisplayName("test showUpdateForm should return \"ruleName/update\"")
        public void showUpdateFormTestShouldReturnStringRuleNameUpdate() {

            //GIVEN
            RuleName ruleName = new RuleName();
            ruleName.setId(1);
            ruleName.setName("Rule Name");
            ruleName.setDescription("Rule Description");
            ruleName.setJson("Json");
            ruleName.setTemplate("Template");
            ruleName.setSqlStr("SQL");
            ruleName.setSqlPart("SQL Part");
            ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<RuleName> ruleNameArgumentCaptor = ArgumentCaptor.forClass(RuleName.class);
            when(ruleNameService.getRuleNameById(anyInt())).thenReturn(ruleName);

            //WHEN
            String html = ruleNameController.showUpdateForm(1, model, request);

            //THEN
            verify(model).addAttribute(stringArgumentCaptor.capture(), ruleNameArgumentCaptor.capture()); //times(1) is used by default
            assertThat(stringArgumentCaptor.getValue()).isEqualTo("ruleName");
            assertThat(ruleNameArgumentCaptor.getValue()).isEqualTo(ruleName);
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
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class updateBidRuleNameControllerTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/ruleName/update/");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

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
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class deleteBidRuleNameControllerTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/ruleName/delete/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

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
