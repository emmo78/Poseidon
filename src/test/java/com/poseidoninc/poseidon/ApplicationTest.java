package com.poseidoninc.poseidon;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("mytest")
public class ApplicationTest {
        @Test
        void contextLoads() {
            log.info("PatientServiceApplicationTests");
        }

}
