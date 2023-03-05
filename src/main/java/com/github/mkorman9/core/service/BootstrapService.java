package com.github.mkorman9.core.service;

import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class BootstrapService {
    private static final Logger LOG = LoggerFactory.getLogger(BootstrapService.class);

    public void startup(@Observes StartupEvent startupEvent) {
        LOG.info("Starting up");
    }
}
