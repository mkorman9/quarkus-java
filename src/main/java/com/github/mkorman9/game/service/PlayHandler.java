package com.github.mkorman9.game.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PlayHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PlayHandler.class);

    @Inject
    PacketSender sender;

}
