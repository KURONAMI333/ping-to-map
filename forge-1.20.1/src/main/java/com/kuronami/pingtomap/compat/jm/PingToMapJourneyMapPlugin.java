package com.kuronami.pingtomap.compat.jm;

import com.kuronami.pingtomap.PingToMap;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;

/**
 * JourneyMap 1.20.1 plugin entry (v1 API).
 */
@ClientPlugin
public class PingToMapJourneyMapPlugin implements IClientPlugin {

    public static volatile IClientAPI api;

    @Override
    public void initialize(IClientAPI jmClientApi) {
        api = jmClientApi;
        PingToMap.LOGGER.info("JourneyMap API initialized for Ping to Map");
    }

    @Override
    public String getModId() {
        return PingToMap.MODID;
    }

    @Override
    public void onEvent(ClientEvent event) {
        // 未使用
    }
}
