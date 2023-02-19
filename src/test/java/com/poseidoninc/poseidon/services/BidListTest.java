package com.poseidoninc.poseidon.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.repositories.BidListRepository;

@ExtendWith(MockitoExtension.class)
public class BidListTest {
	@InjectMocks
	private BidListServiceImpl bidListService;
	
	@Mock
	private BidListRepository bidListRepository;
	
	@Spy
	private final RequestService requestService = new RequestServiceImpl();
	
	private MockHttpServletRequest requestMock;
	private WebRequest request;
	private BidList bidList;

	@Nested
	@Tag("getBidListByIdTests")
	@DisplayName("Tests for getting bidList by Id")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetBidListByIdTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/getById/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@AfterEach
		public void unSetForEachTests() {
			bidListService = null;
			bidList = null;
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListById should return expected bidList")
		public void getBidListByIdTestShouldRetrunExcpectedBidList() {
			
			//GIVEN
			bidList = new BidList();
			bidList.setBidListId(1);
			bidList.setAccount("acount");
			bidList.setType("type");
			bidList.setBidQuantity(1.0);
			bidList.setAskQuantity("USER");
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(bidList));
			
			//WHEN
			BidList bidListResult = bidListService.getBidListById(1, request);
			
			//THEN
			assertThat(bidListResult).extracting(
					BidList::getId,
					BidList::getBidListname,
					BidList::getPassword,
					BidList::getFullname,
					BidList::getRole)
				.containsExactly(
					1,
					"Aaa",
					"aaa1=Passwd",
					"AAA",
					"USER");	
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListById should throw IllegalArgumentException")
		public void getBidListByIdTestShouldThrowsUnexpectedRollbackExceptionOnIllegalArgumentException() {
			//GIVEN
			when(bidListRepository.findById(nullable(Integer.class))).thenThrow(new IllegalArgumentException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(IllegalArgumentException.class,
				() -> bidListService.getBidListById(null, request))
				.getMessage()).isEqualTo("Id must not be null");
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListById should throw ResourceNotFoundException")
		public void getBidListByIdTestShouldThrowsResourceNotFoundException() {
			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.ofNullable(null));
			
			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
				() -> bidListService.getBidListById(1, request))
				.getMessage()).isEqualTo("BidList not found");
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListById should throw UnexpectedRollbackException on any RuntimeException")
		public void getBidListByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(bidListRepository.findById(anyInt())).thenThrow(new RuntimeException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> bidListService.getBidListById(1, request))
				.getMessage()).isEqualTo("Error while getting bidList profile");
		}
	}
	
	@Nested
	@Tag("getBidListByIdWithBlankPasswdTests")
	@DisplayName("Tests for getting bidList by Id with blank passwd")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetBidListByIdWithBlankPasswdTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/getById/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@AfterEach
		public void unSetForEachTests() {
			bidListService = null;
			bidList = null;
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListByIdWithBlankPasswd should return expected bidList")
		public void getBidListByIdWithBlankPasswdTestShouldRetrunExcpectedBidList() {
			
			//GIVEN
			bidList = new BidList();
			bidList.setId(1);
			bidList.setBidListname("Aaa");
			bidList.setPassword("aaa1=Passwd");
			bidList.setFullname("AAA");
			bidList.setRole("USER");
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(bidList));
			
			//WHEN
			BidList bidListResult = bidListService.getBidListByIdWithBlankPasswd(1, request);
			
			//THEN
			assertThat(bidListResult).extracting(
					BidList::getId,
					BidList::getBidListname,
					BidList::getPassword,
					BidList::getFullname,
					BidList::getRole)
				.containsExactly(
					1,
					"Aaa",
					"",
					"AAA",
					"USER");	
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListByIdWithBlankPasswd should throw IllegalArgumentException")
		public void getBidListByIdWithBlankPasswdTestShouldThrowsUnexpectedRollbackExceptionOnIllegalArgumentException() {
			//GIVEN
			when(bidListRepository.findById(anyInt())).thenThrow(new IllegalArgumentException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(IllegalArgumentException.class,
				() -> bidListService.getBidListByIdWithBlankPasswd(1, request))
				.getMessage()).isEqualTo("Id must not be null");
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListByIdWithBlankPasswd should throw ResourceNotFoundException")
		public void getBidListByIdWithBlankPasswdTestShouldThrowsResourceNotFoundException() {
			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.ofNullable(null));
			
			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
				() -> bidListService.getBidListByIdWithBlankPasswd(1, request))
				.getMessage()).isEqualTo("BidList not found");
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListByIdWithBlankPasswd should throw UnexpectedRollbackException on any RuntimeException")
		public void getBidListByIdWithBlankPasswdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(bidListRepository.findById(anyInt())).thenThrow(new RuntimeException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> bidListService.getBidListByIdWithBlankPasswd(1, request))
				.getMessage()).isEqualTo("Error while getting bidList profile");
		}
	}
	
	@Nested
	@Tag("getBidListsTests")
	@DisplayName("Tests for getting bidLists")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetBidListsTests {
		
		private Pageable pageRequest;
		
		@BeforeAll
		public void setUpForAllTests() {
			pageRequest = Pageable.unpaged();
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/getBidLists");
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
			bidListService = null;
			bidList = null;
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidLists should return bidLists")
		public void getBidListsTesthouldRetrunExcpectedBidLists() {
			
			//GIVEN
			List<BidList> expectedBidLists = new ArrayList<>();
			bidList = new BidList();
			bidList.setId(1);
			bidList.setBidListname("Aaa");
			bidList.setPassword("aaa1=Passwd");
			bidList.setFullname("AAA");
			bidList.setRole("USER");
			expectedBidLists.add(bidList);
			bidList.setId(2);
			bidList.setBidListname("Bbb");
			bidList.setPassword("bbb2=Passwd");
			bidList.setFullname("BBB");
			expectedBidLists.add(bidList);
			when(bidListRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<BidList>(expectedBidLists, pageRequest, 2));
			
			//WHEN
			Page<BidList> resultedBidLists = bidListService.getBidLists(pageRequest, request);
			
			//THEN
			assertThat(resultedBidLists).containsExactlyElementsOf(expectedBidLists);
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidLists should throw UnexpectedRollbackException on NullPointerException")
		public void getBidListsTestShouldThrowsUnexpectedRollbackExceptionOnNullPointerException() {
			//GIVEN
			when(bidListRepository.findAll(any(Pageable.class))).thenThrow(new NullPointerException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.getBidLists(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting BidLists");
		}
		
		@Test
		@Tag("RegisteredServiceTest")
		@DisplayName("test getBidLists should throw UnexpectedRollbackException on any RuntimeException")
		public void getBidListsTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(bidListRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.getBidLists(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting BidLists");
		}
	}
	
	@Nested
	@Tag("saveBidListTests")
	@DisplayName("Tests for saving bidLists")
	@TestInstance(Lifecycle.PER_CLASS)
	class SaveBidListTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/saveBidList/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@BeforeEach
		public void setUpForEachTest() {
			bidList = new BidList();
			bidList.setId(1);
			bidList.setBidListname("Aaa");
			bidList.setPassword("aaa1=Passwd");
			bidList.setFullname("AAA");
			bidList.setRole("USER");
		}
		
		@AfterEach
		public void unSetForEachTests() {
			bidListService = null;
			bidList = null;
		}

		@ParameterizedTest(name = "id = {0}, bidListToSaveName = {1}, existsByBidListName return {2}, saveBidList should return bidList")
		@CsvSource(value = {"null, Aaa, false",
							"1, Aaa, true",
							"1, Bbb, false"}
							,nullValues = {"null"})
		@Tag("BidListServiceTest")
		@DisplayName("test saveBidList should return bidList")
		public void saveBidListTestShouldReturnBidList(Integer id, String bidListToSaveName, boolean existsByBidListName) {
			
			//GIVEN
			BidList bidListToSave = new BidList();
			bidListToSave.setId(id);
			bidListToSave.setBidListname(bidListToSaveName);
			bidListToSave.setPassword("aaa1=Passwd");
			bidListToSave.setFullname("AAA");
			bidListToSave.setRole("USER");
			
			when(bidListRepository.findById(nullable(Integer.class))).then(invocation -> {
				Integer index = invocation.getArgument(0);
				if (index == null) {
					throw new IllegalArgumentException ("Id must not be null");
				} else {
					return Optional.of(bidList); 
				}
			});
			lenient().when(bidListRepository.existsByBidListname(anyString())).thenReturn(existsByBidListName);
			ArgumentCaptor<BidList> bidListBeingSaved = ArgumentCaptor.forClass(BidList.class);
			when(bidListRepository.save(any(BidList.class))).thenReturn(bidListToSave);
			
			//WHEN
			BidList resultedBidList = bidListService.saveBidList(bidListToSave, request);
			
			//THEN
			verify(bidListRepository, times(1)).save(bidListBeingSaved.capture());
			passwordEncoder.matches("aaa1=Passwd", bidListBeingSaved.getValue().getPassword());
			assertThat(resultedBidList).extracting(
					BidList::getBidListname,
					BidList::getFullname,
					BidList::getRole)
				.containsExactly(
					bidListToSaveName,
					"AAA",
					"USER");	
		}
		
		@ParameterizedTest(name = "id = {0}, bidListToSaveName = {1}, existsByBidListName return {2}, saveBidList should throw ResourceConflictException")
		@CsvSource(value = {"null, Aaa, true",
							"1, Bbb, true"}
							,nullValues = {"null"})
		@Tag("BidListServiceTest")
		@DisplayName("test saveBidList should throw ResourceConflictException")
		public void saveBidListTestShouldThrowsResourceConflictException(Integer id, String bidListToSaveName, boolean existsByBidListName) {

			//GIVEN
			BidList bidListToSave = new BidList();
			bidListToSave.setId(id);
			bidListToSave.setBidListname(bidListToSaveName);
			bidListToSave.setPassword("aaa1=Passwd");
			bidListToSave.setFullname("AAA");
			bidListToSave.setRole("USER");

			when(bidListRepository.findById(nullable(Integer.class))).thenReturn(Optional.of(bidList));
			lenient().when(bidListRepository.existsByBidListname(anyString())).thenReturn(existsByBidListName);

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceConflictException.class,
					() -> bidListService.saveBidList(bidListToSave, request))
					.getMessage()).isEqualTo("BidListName already exists");
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test saveBidList with unknow id should throw a ResourceNotFoundException")
		public void saveBidListTestWithUnknownIdShouldThrowAResourceNotFoundException() {
			
			//GIVEN
			BidList bidListToSave = new BidList();
			bidListToSave.setId(1);
			bidListToSave.setBidListname("Aaa");
			bidListToSave.setPassword("aaa1=Passwd");
			bidListToSave.setFullname("AAA");
			bidListToSave.setRole("USER");

			when(bidListRepository.findById(nullable(Integer.class))).thenThrow(new ResourceNotFoundException("BidList not found"));

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
					() -> bidListService.saveBidList(bidListToSave, request))
					.getMessage()).isEqualTo("BidList not found");
		}	
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test saveBidList should throw UnexpectedRollbackException on any RuntimeException")
		public void saveBidListTestShouldThrowUnexpectedRollbackExceptionOnAnyRuntimeException() {
			
			//GIVEN
			BidList bidListToSave = new BidList();
			bidListToSave.setId(1);
			bidListToSave.setBidListname("Aaa");
			bidListToSave.setPassword("aaa1=Passwd");
			bidListToSave.setFullname("AAA");
			bidListToSave.setRole("USER");

			when(bidListRepository.findById(nullable(Integer.class))).thenReturn(Optional.of(bidList));
			lenient().when(bidListRepository.existsByBidListname(anyString())).thenReturn(true);
			when(bidListRepository.save(any(BidList.class))).thenThrow(new RuntimeException());

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.saveBidList(bidListToSave, request))
					.getMessage()).isEqualTo("Error while saving bidList");
		}	
	}
	
	@Nested
	@Tag("deleteBidListTests")
	@DisplayName("Tests for deleting bidLists")
	@TestInstance(Lifecycle.PER_CLASS)
	class DeleteBidListTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/delete/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@BeforeEach
		public void setUpForEachTest() {
			bidList = new BidList();
			bidList.setId(1);
			bidList.setBidListname("Aaa");
			bidList.setPassword("aaa1=Passwd");
			bidList.setFullname("AAA");
			bidList.setRole("USER");
		}
		
		@AfterEach
		public void unSetForEachTests() {
			bidListService = null;
			bidList = null;
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test deleteBidList should delete it")
		public void deleteBidListTestShouldDeleteIt() {
			
			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(bidList));
			ArgumentCaptor<BidList> bidListBeingDeleted = ArgumentCaptor.forClass(BidList.class);
			doNothing().when(bidListRepository).delete(any(BidList.class));// Needed to Capture bidList
			
			//WHEN
			bidListService.deleteBidList(1, request);
			
			//THEN
			verify(bidListRepository, times(1)).delete(bidListBeingDeleted.capture());
			assertThat(bidListBeingDeleted.getValue()).extracting(
					BidList::getId,
					BidList::getBidListname,
					BidList::getPassword,
					BidList::getFullname,
					BidList::getRole)
				.containsExactly(
					1,
					"Aaa",
					"aaa1=Passwd",
					"AAA",
					"USER");	
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test deleteBidList should throw ResourceNotFoundException")
		public void saveBidListTestShouldThrowUnexpectedRollbackExceptionOnResourceNotFoundException() {

			//GIVEN
			when(bidListRepository.findById(anyInt())).thenThrow(new ResourceNotFoundException("BidList not found"));

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
					() -> bidListService.deleteBidList(2, request))
					.getMessage()).isEqualTo("BidList not found");
		}	

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test deleteBidList should throw UnexpectedRollbackException On Any RuntimeExpceptioin")
		public void deleteBidListTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {

			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(bidList));
			doThrow(new RuntimeException()).when(bidListRepository).delete(any(BidList.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.deleteBidList(1, request))
					.getMessage()).isEqualTo("Error while deleting bidList");
		}	
	}

}
