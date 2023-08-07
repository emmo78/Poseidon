package com.poseidoninc.poseidon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.RuleName;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import com.poseidoninc.poseidon.repository.RuleNameRepository;

@ExtendWith(MockitoExtension.class)
public class RuleNameServiceTest {
	@InjectMocks
	private RuleNameServiceImpl ruleNameService;
	
	@Mock
	private RuleNameRepository ruleNameRepository;
	
	@Spy
	private final RequestService requestService = new RequestServiceImpl();
	
	private MockHttpServletRequest requestMock;
	private WebRequest request;
	private RuleName ruleName;

	@Nested
	@Tag("getRuleNameByIdTests")
	@DisplayName("Tests for getting ruleName by ruleNameId")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetRuleNameByIdTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/ruleName/getById/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@AfterEach
		public void unSetForEachTests() {
			ruleNameService = null;
			ruleName = null;
		}

		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test getRuleNameById should return expected ruleName")
		public void getRuleNameByIdTestShouldReturnExpectedRuleName() {
			
			//GIVEN
			ruleName = new RuleName();
			ruleName.setId(1);
			ruleName.setName("Rule Name");
			ruleName.setDescription("Rule Description");
			ruleName.setJson("Json");
			ruleName.setTemplate("Template");
			ruleName.setSqlStr("SQL");
			ruleName.setSqlPart("SQL Part");
			when(ruleNameRepository.findById(anyInt())).thenReturn(Optional.of(ruleName));
			
			//WHEN
			RuleName ruleNameResult = ruleNameService.getRuleNameById(1, request);
			
			//THEN
			assertThat(ruleNameResult).extracting(
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
					"SQL Part");		
		}
		
		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test getRuleNameById should throw InvalidDataAccessApiUsageException")
		public void getRuleNameByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {
			//GIVEN
			when(ruleNameRepository.findById(nullable(Integer.class))).thenThrow(new InvalidDataAccessApiUsageException("The given id must not be null"));
			
			//WHEN
			//THEN
			assertThat(assertThrows(InvalidDataAccessApiUsageException.class,
				() -> ruleNameService.getRuleNameById(null, request))
				.getMessage()).isEqualTo("Id must not be null");
		}

		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test getRuleNameById should throw ResourceNotFoundException")
		public void getRuleNameByIdTestShouldThrowsResourceNotFoundException() {
			//GIVEN
			when(ruleNameRepository.findById(anyInt())).thenReturn(Optional.empty());
			
			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
				() -> ruleNameService.getRuleNameById(1, request))
				.getMessage()).isEqualTo("RuleName not found");
		}
		
		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test getRuleNameById should throw UnexpectedRollbackException on any RuntimeException")
		public void getRuleNameByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(ruleNameRepository.findById(anyInt())).thenThrow(new RuntimeException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> ruleNameService.getRuleNameById(1, request))
				.getMessage()).isEqualTo("Error while getting ruleName");
		}
	}
	
	@Nested
	@Tag("getRuleNamesTests")
	@DisplayName("Tests for getting ruleNames")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetRuleNamesTests {
		
		private Pageable pageRequest;
		
		@BeforeAll
		public void setUpForAllTests() {
			pageRequest = Pageable.unpaged();
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/ruleName/getRuleNames");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			pageRequest = null;
			requestMock = null;
			request = null;
		}
		
		@AfterEach
		public void unSetForEachTests() {
			ruleNameService = null;
			ruleName = null;
		}

		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test getRuleNames should return ruleNames")
		public void getRuleNamesTestShouldReturnRuleNames() {
			
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
			when(ruleNameRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<RuleName>(expectedRuleNames, pageRequest, 2));
			
			//WHEN
			Page<RuleName> resultedRuleNames = ruleNameService.getRuleNames(pageRequest, request);
			
			//THEN
			assertThat(resultedRuleNames).containsExactlyElementsOf(expectedRuleNames);
		}
		
		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test getRuleNames should throw UnexpectedRollbackException on NullPointerException")
		public void getRuleNamesTestShouldThrowsUnexpectedRollbackExceptionOnNullPointerException() {
			//GIVEN
			when(ruleNameRepository.findAll(nullable(Pageable.class))).thenThrow(new NullPointerException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ruleNameService.getRuleNames(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting RuleNames");
		}
		
		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test getRuleNames should throw UnexpectedRollbackException on any RuntimeException")
		public void getRuleNamesTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(ruleNameRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ruleNameService.getRuleNames(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting RuleNames");
		}
	}
	
	@Nested
	@Tag("saveRuleNameTests")
	@DisplayName("Tests for saving ruleName")
	@TestInstance(Lifecycle.PER_CLASS)
	class SaveRuleNameTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/ruleName/saveRuleName/");
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
		
		@AfterEach
		public void unSetForEachTests() {
			ruleNameService = null;
			ruleName = null;
		}

		@ParameterizedTest(name = "{0} ruleName to save, so id = {1}, saveRuleName should return ruleName with an id")
		@CsvSource(value = {"new, null", // save new ruleName
							"updated, 1"} // save updated ruleName
							,nullValues = {"null"})
		@Tag("RuleNameServiceTest")
		@DisplayName("test saveRuleName should return ruleName")
		public void saveRuleNameTestShouldReturnRuleName(String state, Integer id) {
			
			//GIVEN
			ruleName.setId(id);
			when(ruleNameRepository.save(any(RuleName.class))).then(invocation -> {
				RuleName ruleNameSaved = invocation.getArgument(0);
				ruleNameSaved.setId(Optional.ofNullable(ruleNameSaved.getId()).orElseGet(() -> 1));
				return ruleNameSaved;
			});
			
			//WHEN
			RuleName ruleNameResult = ruleNameService.saveRuleName(ruleName, request);
			
			//THEN
			verify(ruleNameRepository).save(ruleName); //times(1) is the default and can be omitted
			assertThat(ruleNameResult).extracting(
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
					"SQL Part");		

		}
				
		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test saveRuleName should throw UnexpectedRollbackException on any RuntimeException")
		public void saveRuleNameTestShouldThrowUnexpectedRollbackExceptionOnAnyRuntimeException() {
			
			//GIVEN
			ruleName.setId(1);
			when(ruleNameRepository.save(any(RuleName.class))).thenThrow(new RuntimeException());

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ruleNameService.saveRuleName(ruleName, request))
					.getMessage()).isEqualTo("Error while saving ruleName");
		}	
	}

	@Nested
	@Tag("deleteRuleNameTests")
	@DisplayName("Tests for deleting ruleNames")
	@TestInstance(Lifecycle.PER_CLASS)
	class DeleteRuleNameTests {
		
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
		
		@AfterEach
		public void unSetForEachTests() {
			ruleNameService = null;
			ruleName = null;
		}

		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test deleteRuleName by Id should delete it")
		public void deleteRuleNameByIdTestShouldDeleteIt() {
			
			//GIVEN
			when(ruleNameRepository.findById(anyInt())).thenReturn(Optional.of(ruleName));
			ArgumentCaptor<RuleName> ruleNameBeingDeleted = ArgumentCaptor.forClass(RuleName.class);
			doNothing().when(ruleNameRepository).delete(any(RuleName.class));// Needed to Capture ruleName
			
			//WHEN
			ruleNameService.deleteRuleNameById(1, request);
			
			//THEN
			verify(ruleNameRepository, times(1)).delete(ruleNameBeingDeleted.capture());
			assertThat(ruleNameBeingDeleted.getValue()).extracting(
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
					"SQL Part");		
		}
		
		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test deleteRuleName by Id by Id should throw ResourceNotFoundException")
		public void deleteRuleNameByIdTestShouldThrowUnexpectedRollbackExceptionOnResourceNotFoundException() {

			//GIVEN
			when(ruleNameRepository.findById(anyInt())).thenThrow(new ResourceNotFoundException("RuleName not found"));

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
					() -> ruleNameService.deleteRuleNameById(2, request))
					.getMessage()).isEqualTo("RuleName not found");
		}	

		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test deleteRuleName by Id should throw UnexpectedRollbackException On InvalidDataAccessApiUsageException()")
		public void deleteRuleNameByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {

			//GIVEN
			when(ruleNameRepository.findById(anyInt())).thenReturn(Optional.of(ruleName));
			doThrow(new InvalidDataAccessApiUsageException("The given id must not be null")).when(ruleNameRepository).delete(any(RuleName.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ruleNameService.deleteRuleNameById(1, request))
					.getMessage()).isEqualTo("Error while deleting ruleName");
		}	

		
		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test deleteRuleName by Id should throw UnexpectedRollbackException On Any RuntimeExpceptioin")
		public void deleteRuleNameByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {

			//GIVEN
			when(ruleNameRepository.findById(anyInt())).thenReturn(Optional.of(ruleName));
			doThrow(new RuntimeException()).when(ruleNameRepository).delete(any(RuleName.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ruleNameService.deleteRuleNameById(1, request))
					.getMessage()).isEqualTo("Error while deleting ruleName");
		}	
	}

}
