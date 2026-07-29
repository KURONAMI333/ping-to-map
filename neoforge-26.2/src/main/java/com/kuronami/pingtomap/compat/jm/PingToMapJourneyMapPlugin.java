package com.kuronami.pingtomap.compat.jm;

import com.kuronami.pingtomap.PingToMap;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.common.JourneyMapPlugin;

/**
 * JourneyMap が起動時に検出して呼び出すプラグインエントリ。
 * IClientAPI ハンドルを保持し、後で waypoint 登録・削除に使う。
 */
@JourneyMapPlugin(apiVersion = IClientAPI.API_VERSION)
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
}
