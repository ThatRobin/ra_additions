package io.github.thatrobin.ra_additions;

import io.github.apace100.apoli.util.NamespaceAlias;
import io.github.apace100.calio.resource.OrderedResourceListenerInitializer;
import io.github.apace100.calio.resource.OrderedResourceListenerManager;
import io.github.thatrobin.ra_additions.commands.*;
import io.github.thatrobin.ra_additions.networking.RAA_ModPacketC2S;
import io.github.thatrobin.ra_additions.powers.factories.*;
import io.github.thatrobin.ra_additions.registry.ItemRegistry;
import io.github.thatrobin.ra_additions.util.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class RA_Additions implements ModInitializer, OrderedResourceListenerInitializer {

    public static final Logger LOGGER = LogManager.getLogger(RA_Additions.class);
    public static int[] SEMVER;
    public static String MODID = "ra_additions";
    public static String VERSION = "";

    @Override
    public void onInitialize() {
        FabricLoader.getInstance().getModContainer(MODID).ifPresent(modContainer -> {
            VERSION = modContainer.getMetadata().getVersion().getFriendlyString();
            if(VERSION.contains("+")) {
                VERSION = VERSION.split("\\+")[0];
            }
            if(VERSION.contains("-")) {
                VERSION = VERSION.split("-")[0];
            }
            String[] splitVersion = VERSION.split("\\.");
            SEMVER = new int[splitVersion.length];
            for(int i = 0; i < SEMVER.length; i++) {
                SEMVER[i] = Integer.parseInt(splitVersion[i]);
            }
        });
        PowerFactories.register();
        EntityConditions.register();
        EntityActions.register();
        BiEntityConditions.register();
        BiEntityActions.register();
        BlockConditions.register();
        ItemConditions.register();
        ItemActions.register();

        GeckoLib.initialize();
        ItemRegistry.register();
        NamespaceAlias.addAlias(MODID, "apoli");
        NamespaceAlias.addAlias("origins", "apoli");

        RAA_ClassDataRegistry.registerAll();

        RAA_ModPacketC2S.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> {
            DataCommandExtension.register(dispatcher);
            ExecuteCommandExtension.register(dispatcher);
        });

        RAAEntitySelectorOptions.register();

        ServerWorldEvents.UNLOAD.register(((server, world) -> KeybindRegistry.clear()));
    }

    public static Identifier identifier(String path) {
        return new Identifier("ra_additions", path);
    }

    public static Path getExamplePathRoot() {
        try {
            return Path.of(new File("../../../").getCanonicalPath()).resolve("ra_additions");
        } catch (IOException e) {
            LOGGER.info("Path not found: " + e);
            return null;
        }
    }

    @Override
    public void registerResourceListeners(OrderedResourceListenerManager manager) {
        manager.register(ResourceType.SERVER_DATA, new KeybindManager()).complete();
    }
}
