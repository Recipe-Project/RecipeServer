package com.recipe.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = "classpath:application-test.yml")
@SpringBootTest
class AppApplicationTests {

    @Test
    void contextLoads() {
    }

}
