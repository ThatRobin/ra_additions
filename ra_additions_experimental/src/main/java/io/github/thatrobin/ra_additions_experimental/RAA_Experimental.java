package io.github.thatrobin.ra_additions_experimental;

import io.github.apace100.calio.resource.OrderedResourceListenerInitializer;
import io.github.apace100.calio.resource.OrderedResourceListenerManager;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_experimental.commands.MechanicTypeArgumentType;
import io.github.thatrobin.ra_additions_experimental.commands.RAADataCommand;
import io.github.thatrobin.ra_additions_experimental.factories.mechanics.MechanicFactories;
import io.github.thatrobin.ra_additions_experimental.factories.mechanics.MechanicManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.resource.ResourceType;

public class RAA_Experimental implements ModInitializer, OrderedResourceListenerInitializer {
    @Override
    public void onInitialize() {
        MechanicFactories.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> {
            RAADataCommand.register(dispatcher);
        });

        ArgumentTypeRegistry.registerArgumentType(RA_Additions.identifier("mechanic"), MechanicTypeArgumentType.class, ConstantArgumentSerializer.of((test) -> MechanicTypeArgumentType.power()));

    }

    @Override
    public void registerResourceListeners(OrderedResourceListenerManager manager) {
        manager.register(ResourceType.SERVER_DATA, new MechanicManager()).complete();
    }
}
