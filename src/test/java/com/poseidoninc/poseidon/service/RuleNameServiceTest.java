package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.repository.RuleNameRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleNameServiceTest {
	@InjectMocks
	private RuleNameServiceImpl ruleNameService;
	
	@Mock
	private RuleNameRepository ruleNameRepository;
	
	private RuleName ruleName;

	@Nested
	@Tag("getRuleNameByIdTests")
	@DisplayName("Tests for getting ruleName by ruleNameId")
	class GetRuleNameByIdTests {
		
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
			RuleName ruleNameResult = ruleNameService.getRuleNameById(1);
			
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
		@DisplayName("test getRuleNameById should throw UnexpectedRollbackException on InvalidDataAccessApiUsageException")
		public void getRuleNameByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {
			//GIVEN
			when(ruleNameRepository.findById(nullable(Integer.class))).thenThrow(new InvalidDataAccessApiUsageException("The given id must not be null"));
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> ruleNameService.getRuleNameById(null))
				.getMessage()).isEqualTo("Error while getting ruleName");
		}

		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test getRuleNameById should throw UnexpectedRollbackException on  ResourceNotFoundException")
		public void getRuleNameByIdTestShouldThrowsUnexpectedRollbackExceptionOnResourceNotFoundException() {
			//GIVEN
			when(ruleNameRepository.findById(anyInt())).thenReturn(Optional.empty());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> ruleNameService.getRuleNameById(1))
				.getMessage()).isEqualTo("Error while getting ruleName");
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
				() -> ruleNameService.getRuleNameById(1))
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
		}

		@AfterAll
		public void unSetForAllTests() {
			pageRequest = null;
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
			Page<RuleName> resultedRuleNames = ruleNameService.getRuleNames(pageRequest);
			
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
					() -> ruleNameService.getRuleNames(pageRequest))
					.getMessage()).isEqualTo("Error while getting ruleNames");
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
					() -> ruleNameService.getRuleNames(pageRequest))
					.getMessage()).isEqualTo("Error while getting ruleNames");
		}
	}
	
	@Nested
	@Tag("saveRuleNameTests")
	@DisplayName("Tests for saving ruleName")
	class SaveRuleNameTests {

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

		@Test
		@Tag("RuleNameServiceTest")
		@DisplayName("test saveRuleName should return ruleName")
		public void saveRuleNameTestShouldReturnRuleName() {
			
			//GIVEN
			when(ruleNameRepository.save(any(RuleName.class))).then(invocation -> {
				RuleName ruleNameSaved = invocation.getArgument(0);
				ruleNameSaved.setId(1);
				return ruleNameSaved;
			});
			
			//WHEN
			RuleName ruleNameResult = ruleNameService.saveRuleName(ruleName);
			
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
					() -> ruleNameService.saveRuleName(ruleName))
					.getMessage()).isEqualTo("Error while saving ruleName");
		}	
	}

	@Nested
	@Tag("deleteRuleNameTests")
	@DisplayName("Tests for deleting ruleNames")
	class DeleteRuleNameTests {

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
			ruleNameService.deleteRuleNameById(1);
			
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
		@DisplayName("test deleteRuleName by Id by Id should throw  UnexpectedRollbackException on UnexpectedRollbackException")
		public void deleteRuleNameByIdTestShouldThrowUnexpectedRollbackExceptionOnUnexpectedRollbackException() {

			//GIVEN
			when(ruleNameRepository.findById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting ruleName"));

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ruleNameService.deleteRuleNameById(2))
					.getMessage()).isEqualTo("Error while deleting ruleName");
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
					() -> ruleNameService.deleteRuleNameById(1))
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
					() -> ruleNameService.deleteRuleNameById(1))
					.getMessage()).isEqualTo("Error while deleting ruleName");
		}	
	}

}
