package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.service.RuleNameService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
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

	@AfterEach
	public void unsetForEachTest() {
		ruleNameController = null;
	}

	@Test
	@Tag("RuleNameControllerTest")
	@DisplayName("test home should return \"ruleName/list\"")
	public void homeTest() {
		
		//GIVEN
		//WHEN
		String html = ruleNameController.home(model, request);
		
		//THEN
		assertThat(html).isEqualTo("ruleName/list");
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
	@Test
	@Tag("RuleNameControllerTest")
	@DisplayName("test validate should return \"redirect:/ruleName/list\"")
	public void validateTestShouldReturnStringRedirectRuleNameList() {

		//GIVEN
		RuleName ruleName = new RuleName();
		when(bindingResult.hasErrors()).thenReturn(false);

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
	@DisplayName("test showUpdateForm should return \"ruleName/update\"")
	public void showUpdateFormTestShouldReturnStringRuleNameUpdate() {

		//GIVEN
		RuleName  ruleName = new RuleName();

		//WHEN
		String html = ruleNameController.showUpdateForm(1, model, request);

		//THEN
		assertThat(html).isEqualTo("ruleName/update");
	}

	@Test
	@Tag("RuleNameControllerTest")
	@DisplayName("test update RuleName should return \"redirect:/ruleName/list\"")
	public void updateCurveTestShouldReturnStringRedirectRuleNameList() {

		//GIVEN
		 RuleName ruleName = new RuleName();
		when(bindingResult.hasErrors()).thenReturn(false);

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
	@DisplayName("test delete RuleName should return \"redirect:/ruleName/list\"")
	public void deleteCurveTestShouldReturnStringRedirectCurvePointList() {

		//GIVEN
		//WHEN
		String html = ruleNameController.deleteRuleName(1, request);

		//THEN
		assertThat(html).isEqualTo("redirect:/ruleName/list");
	}
}
