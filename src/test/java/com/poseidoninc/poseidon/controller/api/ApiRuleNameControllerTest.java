package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.RuleNameService;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RequestServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiRuleNameControllerTest {

    @InjectMocks
    private ApiRuleNameController apiRuleNameController;

    @Mock
    private RuleNameService ruleNameService;

    @Spy
    private final RequestService requestService = new RequestServiceImpl();

    private MockHttpServletRequest requestMock;
    private WebRequest request;
    private RuleName ruleName;

    @AfterEach
    public void unsetForEachTest() {
        ruleNameService = null;
        apiRuleNameController = null;
        ruleName = null;
    }

    @Nested
    @Tag("getRuleNames")
    @DisplayName("Tests for /api/ruleName/list")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetRuleNamesTests {

        private Pageable pageRequest;

        @BeforeAll
        public void setUpForAllTests() {
            pageRequest = Pageable.unpaged();
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/ruleName/list");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            pageRequest = null;
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiRuleNameControllerTest")
        @DisplayName("test getRuleNames should return a Success ResponseEntity With Iterable Of RuleName")
        public void getRuleNamesTestShouldReturnSuccessResponseEntityWithIterableOfRuleName() {

            //GIVEN
            List<RuleName> expectedRuleNames = new ArrayList<>();
            ruleName = new RuleName();
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

            when(ruleNameService.getRuleNames(any(Pageable.class))).thenReturn(new PageImpl<RuleName>(expectedRuleNames, pageRequest, 2));

            //WHEN
            ResponseEntity<Iterable<RuleName>> responseEntity = apiRuleNameController.getRuleNames(request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Iterable<RuleName> resultRuleNames = responseEntity.getBody();
            assertThat(resultRuleNames).isNotNull();
            assertThat(resultRuleNames).containsExactlyElementsOf(expectedRuleNames);
        }

        @Test
        @Tag("ApiRuleNameControllerTest")
        @DisplayName("test getRuleNames should throw UnexpectedRollbackException")
        public void getRuleNamesTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(ruleNameService.getRuleNames(any(Pageable.class))).thenThrow(new UnexpectedRollbackException("Error while getting ruleNames"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiRuleNameController.getRuleNames(request))
                    .getMessage()).isEqualTo("Error while getting ruleNames");
        }
    }

    @Nested
    @Tag("createRuleName")
    @DisplayName("Tests for /api/ruleName/create")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateRuleNameTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("POST");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/ruleName/create");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @BeforeEach
        public void setUpForEachTest() {
            ruleName = new RuleName();
            ruleName.setName("Rule Name");
            ruleName.setDescription("Rule Description");
            ruleName.setJson("Json");
            ruleName.setTemplate("Template");
            ruleName.setSqlStr("SQL");
            ruleName.setSqlPart("SQL Part");
        }

        @Test
        @Tag("ApiRuleNameControllerTest")
        @DisplayName("test createRuleName should return a Success ResponseEntity With Saved RuleName")
        public void createRuleNameTestShouldReturnASuccessResponseEntityWithSavedRuleName() {

            //GIVEN
            Optional<RuleName> optionalRuleName = Optional.of(ruleName);
            RuleName ruleNameExpected = new RuleName();
            ruleNameExpected.setId(1);
            ruleNameExpected.setName("Rule Name");
            ruleNameExpected.setDescription("Rule Description");
            ruleNameExpected.setJson("Json");
            ruleNameExpected.setTemplate("Template");
            ruleNameExpected.setSqlStr("SQL");
            ruleNameExpected.setSqlPart("SQL Part");
            when(ruleNameService.saveRuleName(any(RuleName.class))).thenReturn(ruleNameExpected);

            //WHEN
            ResponseEntity<RuleName> responseEntity = null;
            try {
                responseEntity = apiRuleNameController.createRuleName(optionalRuleName, request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            RuleName resultRuleName = responseEntity.getBody();
            assertThat(resultRuleName).isNotNull();
            assertThat(resultRuleName)
                    .extracting(
                            RuleName::getId,
                            RuleName::getName,
                            RuleName::getDescription,
                            RuleName::getJson,
                            RuleName::getTemplate,
                            RuleName::getSqlStr,
                            RuleName::getSqlPart)
                    .containsExactly(
                            1,
                            "Rule Name",
                            "Rule Description",
                            "Json",
                            "Template",
                            "SQL",
                            "SQL Part"
                    );
        }

        @Test
        @Tag("ApiRuleNameControllerTest")
        @DisplayName("test createRuleName should throw BadRequestException")
        public void createRuleNameTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<RuleName> optionalRuleName = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> apiRuleNameController.createRuleName(optionalRuleName, request))
                    .getMessage()).isEqualTo("Correct request should be a json ruleName body");
        }

        @Test
        @Tag("ApiRuleNameControllerTest")
        @DisplayName("test createRuleName should throw UnexpectedRollbackException")
        public void createRuleNameTestShouldThrowMUnexpectedRollbackException() {

            //GIVEN
            Optional<RuleName> optionalRuleName = Optional.of(ruleName);
            when(ruleNameService.saveRuleName(any(RuleName.class))).thenThrow(new UnexpectedRollbackException("Error while saving ruleName"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiRuleNameController.createRuleName(optionalRuleName, request))
                    .getMessage()).isEqualTo("Error while saving ruleName");
        }
    }

    @Nested
    @Tag("getRuleNameById")
    @DisplayName("Tests for /api/ruleName/update/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class getRuleNameByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/ruleName/update/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiRuleNameControllerTest")
        @DisplayName("test getRuleNameById should return a Success ResponseEntity With RuleName")
        public void getRuleNameByIdTestShouldReturnASuccessResponseEntityWithRuleName() {

            //GIVEN
            ruleName = new RuleName();
            ruleName.setId(1);
            ruleName.setName("Rule Name");
            ruleName.setDescription("Rule Description");
            ruleName.setJson("Json");
            ruleName.setTemplate("Template");
            ruleName.setSqlStr("SQL");
            ruleName.setSqlPart("SQL Part");

            when(ruleNameService.getRuleNameById(anyInt())).thenReturn(ruleName);

            //WHEN
            ResponseEntity<RuleName> responseEntity = apiRuleNameController.getRuleNameById(1, request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            RuleName resultRuleName = responseEntity.getBody();
            assertThat(resultRuleName).isNotNull();
            assertThat(resultRuleName)
                    .extracting(
                            RuleName::getId,
                            RuleName::getName,
                            RuleName::getDescription,
                            RuleName::getJson,
                            RuleName::getTemplate,
                            RuleName::getSqlStr,
                            RuleName::getSqlPart)
                    .containsExactly(
                            1,
                            "Rule Name",
                            "Rule Description",
                            "Json",
                            "Template",
                            "SQL",
                            "SQL Part"
                    );
        }

        @Test
        @Tag("ApiRuleNameControllerTest")
        @DisplayName("test getRuleNameById should throw UnexpectedRollbackException")
        public void getRuleNameByIdTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(ruleNameService.getRuleNameById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting ruleName"));
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiRuleNameController.getRuleNameById(1, request))
                    .getMessage()).isEqualTo("Error while getting ruleName");
        }
    }

    @Nested
    @Tag("updateRuleName")
    @DisplayName("Tests for /api/ruleName/update")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdateRuleNameTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("PUT");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/ruleName/update");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @BeforeEach
        public void setUpForEachTest() {
            ruleName = new RuleName();
            ruleName.setId(1);
            ruleName.setName("Rule Name");
            ruleName.setDescription("Rule Description");
            ruleName.setJson("Json");
            ruleName.setTemplate("Template");
            ruleName.setSqlStr("SQL");
            ruleName.setSqlPart("SQL Part");
        }

        @Test
        @Tag("ApiRuleNameControllerTest")
        @DisplayName("test updateRuleName should return a Success ResponseEntity With Saved ruleName")
        public void updateRuleNameTestShouldReturnASuccessResponseEntityWithSavedRuleName() {

            //GIVEN
            Optional<RuleName> optionalRuleName = Optional.of(ruleName);
            RuleName ruleNameExpected = new RuleName();
            ruleNameExpected.setId(1);
            ruleNameExpected.setName("Rule Name");
            ruleNameExpected.setDescription("Rule Description");
            ruleNameExpected.setJson("Json");
            ruleNameExpected.setTemplate("Template");
            ruleNameExpected.setSqlStr("SQL");
            ruleNameExpected.setSqlPart("SQL Part");
            when(ruleNameService.saveRuleName(any(RuleName.class))).thenReturn(ruleNameExpected);

            //WHEN
            ResponseEntity<RuleName> responseEntity = null;
            try {
                responseEntity = apiRuleNameController.updateRuleName(optionalRuleName, request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            RuleName resultRuleName = responseEntity.getBody();
            assertThat(resultRuleName).isNotNull();
            assertThat(resultRuleName)
                    .extracting(
                            RuleName::getId,
                            RuleName::getName,
                            RuleName::getDescription,
                            RuleName::getJson,
                            RuleName::getTemplate,
                            RuleName::getSqlStr,
                            RuleName::getSqlPart)
                    .containsExactly(
                            1,
                            "Rule Name",
                            "Rule Description",
                            "Json",
                            "Template",
                            "SQL",
                            "SQL Part"
                    );
        }

        @Test
        @Tag("ApiRuleNameControllerTest")
        @DisplayName("test updateRuleName should throw BadRequestException")
        public void updateRuleNameTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<RuleName> optionalRuleName = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> apiRuleNameController.updateRuleName(optionalRuleName, request))
                    .getMessage()).isEqualTo("Correct request should be a json ruleName body");
        }

        @Test
        @Tag("ApiRuleNameControllerTest")
        @DisplayName("test updateRuleName should throw UnexpectedRollbackException")
        public void updateRuleNameTestShouldThrowMUnexpectedRollbackException() {

            //GIVEN
            Optional<RuleName> optionalRuleName = Optional.of(ruleName);
            when(ruleNameService.saveRuleName(any(RuleName.class))).thenThrow(new UnexpectedRollbackException("Error while saving ruleName"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiRuleNameController.updateRuleName(optionalRuleName, request))
                    .getMessage()).isEqualTo("Error while saving ruleName");
        }
    }

    @Nested
    @Tag("deleteByIdTests")
    @DisplayName("Tests for /api/ruleName/delete/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("DELETE");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/ruleName/delete/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiRuleNameControllerTest")
        @DisplayName("test deleteById should return HttpStatus.OK")
        public void deleteByIdTestShouldReturnHttpStatusOK() {

            //GIVEN
            //WHEN
            HttpStatus httpStatus = apiRuleNameController.deleteRuleNameById(1, request);

            //THEN
            assertThat(httpStatus.is2xxSuccessful()).isTrue();
        }

        @Test
        @Tag("ApiRuleNameControllerTest")
        @DisplayName("test deleteById should throw UnexpectedRollbackException")
        public void deleteByIdTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            doThrow(new UnexpectedRollbackException("Error while deleting ruleName")).when(ruleNameService).deleteRuleNameById(anyInt());
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiRuleNameController.deleteRuleNameById(1, request))
                    .getMessage()).isEqualTo("Error while deleting ruleName");
        }
    }
}
