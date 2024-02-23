package com.poseidoninc.poseidon.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.poseidoninc.poseidon.domain.BidList;

import jakarta.validation.ConstraintViolationException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
public class BidListServiceIT {

	@Autowired
	private BidListRepository bidListRepository;
	
	private BidList bidList;
	
	@BeforeEach
	public void setUpPerTest() {
		bidList = new BidList();
	}
	
	@AfterEach
	public void undefPerTest() {
		bidListRepository.deleteAll();
		bidList = null;
	}
	
	@Test
	@Tag("BidListRepositoryIT")
	@DisplayName("save test should persist bidList with new id")
	public void saveShouldPersistBidListWithNewId() {

		//GIVEN
		bidList.setBidListId(null);
		bidList.setAccount("account");
		bidList.setType("type");
		bidList.setBidQuantity(1.0);
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
		BidList bidListResult = bidListRepository.saveAndFlush(bidList);
		
		//THEN
		Optional<Integer> idOpt = Optional.ofNullable(bidListResult.getBidListId());
		assertThat(idOpt).isPresent();
		idOpt.ifPresent(id -> assertThat(bidListRepository.findById(id)).get().extracting(
				BidList::getAccount,
				BidList::getType,
				BidList::getBidQuantity,
				BidList::getAskQuantity,
				BidList::getBid,
				BidList::getAsk,
				BidList::getBenchmark,
				bid -> bid.getBidListDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
				BidList::getCommentary,
				BidList::getSecurity,
				BidList::getStatus,
				BidList::getTrader,
				BidList::getBook,
				BidList::getCreationName,
				bid -> bid.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
				BidList::getRevisionName,
				bid -> bid.getRevisionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
				BidList::getDealName,
				BidList::getDealType,
				BidList::getSourceListId,
				BidList::getSide)
			.containsExactly(
					"account",
					"type",
					1.0,
					3.0,
					4.0,
					5.0,
					"benchmark",
					"21/01/2023 10:20:30",
					"commentary",
					"security",
					"status",
					"trader",
					"book",
					"creation name",
					"22/01/2023 12:22:32",
					"revisionName",
					"23/01/2023 13:23:33",
					"deal name",
					"deal type",
					"source list id",
					"side"));
	}
}


