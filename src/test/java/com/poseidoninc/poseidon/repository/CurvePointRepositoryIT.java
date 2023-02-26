package com.poseidoninc.poseidon.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.repositories.CurvePointRepository;

@SpringBootTest
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
	@Tag("existsByCurveIdTests")
	@DisplayName("Tests exists by curvePointId")
	@TestInstance(Lifecycle.PER_CLASS)
	class ExistsByUserNameTests {
		
		@Test
		@Tag("UserRepositoryIT")
		@DisplayName("exists by CurveId should return true")
		public void findByCurveIdTestShouldReturnTrue() {
	
			//GIVEN
			curvePoint.setId(null);
			curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePointRepository.saveAndFlush(curvePoint);
	
			//WHEN
			boolean result = curvePointRepository.existsByCurveId(2);
			
			//THEN
			assertThat(result).isTrue();	
		}
		
		@Test
		@Tag("UserRepositoryIT")
		@DisplayName("exists by CurveId should return false")
		public void findByCurveIdTestShouldReturnFalse() {
	
			//GIVEN
			curvePoint.setId(null);
			curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePointRepository.saveAndFlush(curvePoint);
	
			//WHEN
			boolean result = curvePointRepository.existsByCurveId(3);
			
			//THEN
			assertThat(result).isFalse();	
		}
	}

}
