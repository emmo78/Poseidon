package com.poseidoninc.poseidon.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.poseidoninc.poseidon.domain.CurvePoint;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
public class CurvePointRepositoryIT {

	@Autowired
	private CurvePointRepository curvePointRepository;

	private CurvePoint curvePoint;

	@BeforeEach
	public void setUpPerTest() {
		curvePoint = new CurvePoint();
	}

	@AfterEach
	public void undefPerTest() {
		curvePointRepository.deleteAll();
		curvePoint = null;
	}

	@Nested
	@Tag("saveCurvePointTests")
	@DisplayName("Tests for validation and saving curvePoint")
	class saveCurvePointTests {

		@Test
		@Tag("CurvePointRepositoryIT")
		@DisplayName("save test should persist curvePoint with new id")
		public void saveTestShouldPersistCurvePointWithNewId() {

			//GIVEN
			curvePoint.setId(null);
			curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

			//WHEN
			CurvePoint curvePointResult = curvePointRepository.saveAndFlush(curvePoint);

			//THEN
			Optional<Integer> idOpt = Optional.ofNullable(curvePointResult.getId());
			assertThat(idOpt).isPresent();
			idOpt.ifPresent(id -> assertThat(curvePointRepository.findById(id)).get().extracting(
							CurvePoint::getCurveId,
							curve -> curve.getAsOfDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
							CurvePoint::getTerm,
							CurvePoint::getValue,
							curve -> curve.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
					.containsExactly(
							2,
							"21/01/2023 10:20:30",
							3.0,
							4.0,
							"22/01/2023 12:22:32"
					));
		}

		@Test
		@Tag("CurvePointRepositoryIT")
		@DisplayName("save test update curve point with same curveId should persist it")
		public void saveTestUpdateCurvePointShouldPersistIt() {

			//GIVEN
			curvePoint.setId(null);
			curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			Integer id = curvePointRepository.saveAndFlush(curvePoint).getId();
			CurvePoint updatedCurvePoint = new CurvePoint();
			updatedCurvePoint.setId(id);
			updatedCurvePoint.setCurveId(2);
			updatedCurvePoint.setAsOfDate(LocalDateTime.parse("21/02/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			updatedCurvePoint.setTerm(5.0);
			updatedCurvePoint.setValue(6.0);
			updatedCurvePoint.setCreationDate(LocalDateTime.parse("22/02/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

			//WHEN
			//THEN
			assertThat(assertDoesNotThrow(() -> curvePointRepository.saveAndFlush(updatedCurvePoint))).extracting(
							CurvePoint::getId,
							CurvePoint::getCurveId,
							curve -> curve.getAsOfDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
							CurvePoint::getTerm,
							CurvePoint::getValue,
							curve -> curve.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
					.containsExactly(
							id,
							2,
							"21/02/2023 10:20:30",
							5.0,
							6.0,
							"22/02/2023 12:22:32"
					);
		}

		@Test
		@Tag("CurvePointRepositoryIT")
		@DisplayName("save test a new curvePoint with existent curveId should throw a DataIntegrityViolationException")
		public void saveTestACurvePointWithAnExistentIdShouldThrowDataIntegrityViolationException() {

			//GIVEN
			curvePoint.setId(null);
			curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePointRepository.saveAndFlush(curvePoint);

			CurvePoint curvePointTest = new CurvePoint();
			curvePointTest.setId(null);
			curvePointTest.setCurveId(2);
			curvePointTest.setAsOfDate(LocalDateTime.parse("21/02/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePointTest.setTerm(5.0);
			curvePointTest.setValue(6.0);
			curvePointTest.setCreationDate(LocalDateTime.parse("22/02/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			//WHEN
			//THEN
			assertThat(assertThrows(DataIntegrityViolationException.class,
					() -> curvePointRepository.saveAndFlush(curvePointTest))
					.getMessage()).contains(("Unique index or primary key violation"));
		}
	}
}
