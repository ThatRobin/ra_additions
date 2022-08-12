package io.github.thatrobin.ra_additions.client;

import io.github.thatrobin.ra_additions.networking.RAA_ModPacketS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RA_AdditionsClient implements ClientModInitializer {

    public static boolean isServerRunningCCPacks = false;

    @Override
    public void onInitializeClient() {
        RAA_ModPacketS2C.register();
    }
}
