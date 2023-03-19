package com.github.mkorman9.core;

import com.github.mkorman9.util.PostgresContainer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(PostgresContainer.class)
public class SmokeTest {
    @Test
    public void smokeTest() {
    }
}
