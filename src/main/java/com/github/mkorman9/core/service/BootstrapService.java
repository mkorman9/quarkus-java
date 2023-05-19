package com.github.mkorman9.core.service;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

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
