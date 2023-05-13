package io.github.thatrobin.ra_additions;

import io.github.thatrobin.ra_additions.powers.factories.ItemConditionsServer;
import net.fabricmc.api.DedicatedServerModInitializer;

public class RA_AdditionsServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        ItemConditionsServer.register();
    }
}
