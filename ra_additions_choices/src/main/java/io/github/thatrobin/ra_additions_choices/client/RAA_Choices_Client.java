package io.github.thatrobin.ra_additions_choices.client;

import io.github.thatrobin.ra_additions_choices.networking.RAAC_ModPacketS2C;
import net.fabricmc.api.ClientModInitializer;

public class RAA_Choices_Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RAAC_ModPacketS2C.register();
    }
}
