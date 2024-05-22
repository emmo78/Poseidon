package com.poseidoninc.poseidon;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.repository.BidListRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * provided test for BidRepository
 */
@SpringBootTest
@ActiveProfiles("mytest")
public class BidTests {

	@Autowired
	private BidListRepository bidListRepository;

	@Test
	public void bidListTest() {
		BidList bid = new BidList();
		bid.setAccount("Account Test");
		bid.setType("Type Test");
		bid.setBidQuantity(10d);

		// Save
		bid = bidListRepository.save(bid);
		assertNotNull(bid.getBidListId());
		assertEquals(10d, bid.getBidQuantity());

		// Update
		bid.setBidQuantity(20d);
		bid = bidListRepository.save(bid);
		assertEquals(20d, bid.getBidQuantity());

		// Find
		List<BidList> listResult = bidListRepository.findAll();
		assertTrue(listResult.size() > 0);

		// Delete
		Integer id = bid.getBidListId();
		bidListRepository.delete(bid);
		Optional<BidList> bidList = bidListRepository.findById(id);
		assertThat(bidList).isEmpty();
	}
}
