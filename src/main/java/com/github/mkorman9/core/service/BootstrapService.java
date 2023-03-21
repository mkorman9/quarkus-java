package com.github.mkorman9.core.service;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class BootstrapService {
    private static final Logger LOG = LoggerFactory.getLogger(BootstrapService.class);

    @Inject
    TestDataService testDataService;

    public void startup(@Observes StartupEvent startupEvent) {
        LOG.info("Starting up");

        if (LaunchMode.current() == LaunchMode.DEVELOPMENT) {
            LOG.warn("Using Test Data");
            testDataService.injectTestData();
        }
    }
}
