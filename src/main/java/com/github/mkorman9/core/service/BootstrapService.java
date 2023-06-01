package com.github.mkorman9.core.service;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class BootstrapService {
    @Inject
    TestDataService testDataService;

    public void startup(@Observes StartupEvent startupEvent) {
        log.info("Starting up");

        if (LaunchMode.current() == LaunchMode.DEVELOPMENT) {
            log.warn("Using Test Data");
            testDataService.injectTestData();
        }
    }
}
