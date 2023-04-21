package com.github.mkorman9.game.controller;

import com.github.mkorman9.game.service.PacketSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PlayController {
    private static final Logger LOG = LoggerFactory.getLogger(PlayController.class);

    @Inject
    PacketSender sender;

}
