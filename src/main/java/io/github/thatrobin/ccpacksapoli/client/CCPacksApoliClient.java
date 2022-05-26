package io.github.thatrobin.ccpacksapoli.client;

import io.github.thatrobin.ccpacksapoli.networking.CCPacksModPacketS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CCPacksApoliClient implements ClientModInitializer {

    public static boolean isServerRunningCCPacks = false;

    @Override
    public void onInitializeClient() {
        CCPacksModPacketS2C.register();
    }
}
