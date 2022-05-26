package io.github.thatrobin.ccpacksapoli;

import io.github.apace100.apoli.integration.PostPowerReloadCallback;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.NamespaceAlias;
import io.github.apace100.calio.util.OrderedResourceListeners;
import io.github.thatrobin.ccpacksapoli.choice.Choice;
import io.github.thatrobin.ccpacksapoli.choice.ChoiceLayers;
import io.github.thatrobin.ccpacksapoli.choice.ChoiceManager;
import io.github.thatrobin.ccpacksapoli.commands.ChoiceCommand;
import io.github.thatrobin.ccpacksapoli.commands.LayerArgument;
import io.github.thatrobin.ccpacksapoli.factories.*;
import io.github.thatrobin.ccpacksapoli.networking.CCPacksModPacketC2S;
import io.github.thatrobin.ccpacksapoli.util.UniversalPowerManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CCPacksApoli implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger(CCPacksApoli.class);
    public static int[] SEMVER;
    public static String VERSION = "";

    @Override
    public void onInitialize() {
        FabricLoader.getInstance().getModContainer("ccpacks-apoli").ifPresent(modContainer -> {
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
        Choice.init();

        NamespaceAlias.addAlias("assets/ccpacks", "apoli");
        NamespaceAlias.addAlias("origins", "apoli");

        PowerFactories.register();
        EntityConditions.register();
        EntityActions.register();
        BiEntityActions.register();
        ItemConditions.register();
        ItemActions.register();

        CCPacksModPacketC2S.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            ChoiceCommand.register(dispatcher);
        });

        OrderedResourceListeners.register(new UniversalPowerManager()).after(new Identifier("apoli","powers")).complete();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ChoiceManager());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ChoiceLayers());

        ArgumentTypes.register("ccpacks:choice_layer", LayerArgument.class, new ConstantArgumentSerializer<>(LayerArgument::layer));


    }

    public static Identifier identifier(String path) {
        return new Identifier("ccpacks", path);
    }

}
