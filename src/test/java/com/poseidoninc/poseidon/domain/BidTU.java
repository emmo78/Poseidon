package com.poseidoninc.poseidon.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * unit test class to test constraint for the Bid Entity.
 * @author olivier morel
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BidTU {

    private ValidatorFactory factory;

    private Validator validator;

    private BidList bidList;

    @BeforeAll
    public void setUpForAllTests() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public void undefForAllTests() {
        validator = null;
        factory = null;
    }

    @BeforeEach
    public void setUpPerTest() {
        bidList = new BidList();
    }

    @AfterEach
    public void undefPerTest() {
        bidList = null;
    }

    @ParameterizedTest(name = "{0} account or {1} bid quantity should throw error message {3}")
    @CsvSource(value = {"'', 1.7976931348623157E308, Account is mandatory",
            "' ', 1.7976931348623157E308, Account is mandatory",
            "null, 1.7976931348623157E308, Account is mandatory",
            "AbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyz, 1.7976931348623157E308, Account must be maximum of 30 characters",
            "account, 1.7976931348623159E308, Bidqantity must be a positive decimal number",
            "account, -1.0, Bidqantity must be a positive decimal number"},
            nullValues = {"null"})
    @Tag("BidListRepositoryIT")
    @DisplayName("save test with incorrect value should throw a ConstraintViolationException")
    public void saveTestShouldThrowAConstraintViolationException(String account, Double bidQuantity, String errorMsg) {

        //GIVEN
        bidList.setBidListId(null);
        bidList.setAccount(account);
        bidList.setType("type");
        bidList.setBidQuantity(bidQuantity);
        bidList.setAskQuantity(3.0);
        bidList.setBid(4.0);
        bidList.setAsk(5.0);
        bidList.setBenchmark("benchmark");
        bidList.setBidListDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        bidList.setCommentary("commentary");
        bidList.setSecurity("security");
        bidList.setStatus("status");
        bidList.setTrader("trader");
        bidList.setBook("book");
        bidList.setCreationName("creation name");
        bidList.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        bidList.setRevisionName("revisionName");
        bidList.setRevisionDate(LocalDateTime.parse("23/01/2023 13:23:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        bidList.setDealName("deal name");
        bidList.setDealType("deal type");
        bidList.setSourceListId("source list id");
        bidList.setSide("side");

        //WHEN
        Set<ConstraintViolation<BidList>> constraintViolations = validator.validate(bidList);

        //THEN
        assertThat(constraintViolations).isNotEmpty();
    }
}
