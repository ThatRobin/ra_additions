package io.github.thatrobin.ra_additions_choices;

import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_choices.choice.Choice;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceLayers;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceManager;
import io.github.thatrobin.ra_additions_choices.commands.ChoiceCommand;
import io.github.thatrobin.ra_additions_choices.commands.LayerArgument;
import io.github.thatrobin.ra_additions_choices.factories.EntityActions;
import io.github.thatrobin.ra_additions_choices.networking.RAAC_ModPacketC2S;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.resource.ResourceType;

public class RAA_Choices implements ModInitializer {

    @Override
    public void onInitialize() {
        EntityActions.register();
        Choice.init();

        RAAC_ModPacketC2S.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> {
            ChoiceCommand.register(dispatcher);
        });

        ArgumentTypeRegistry.registerArgumentType(RA_Additions.identifier("choice_layer"), LayerArgument.class, ConstantArgumentSerializer.of((test) -> LayerArgument.layer()));

        registerResourceListeners();
    }

    public void registerResourceListeners() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ChoiceManager());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ChoiceLayers());
    }
}
