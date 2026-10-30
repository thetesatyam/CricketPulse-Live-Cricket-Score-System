package com.cricketpulse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CricketPulseApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring context initializes with all beans and configurations
    }
}
