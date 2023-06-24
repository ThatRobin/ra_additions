package io.github.thatrobin.ra_additions_tags;

import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_tags.commands.*;
import io.github.thatrobin.ra_additions_tags.data_loaders.*;
import io.github.thatrobin.ra_additions_tags.factories.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.resource.ResourceType;

public class RAA_Tags implements ModInitializer {
    @Override
    public void onInitialize() {
        BiEntityActions.register();
        BiEntityConditions.register();
        BlockActions.register();
        BlockConditions.register();
        EntityActions.register();
        EntityConditions.register();
        ItemActions.register();
        ItemConditions.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> {
            RAAPowerCommand.register(dispatcher);
            RAAActionCommand.register(dispatcher);
        });

        ArgumentTypeRegistry.registerArgumentType(RA_Additions.identifier("entity_action_type_layer"), EntityActionTypeArgumentType.class, ConstantArgumentSerializer.of((ignored) -> EntityActionTypeArgumentType.action()));
        ArgumentTypeRegistry.registerArgumentType(RA_Additions.identifier("block_action_type_layer"), BlockActionTypeArgumentType.class, ConstantArgumentSerializer.of((ignored) -> BlockActionTypeArgumentType.action()));
        ArgumentTypeRegistry.registerArgumentType(RA_Additions.identifier("item_action_type_layer"), ItemActionTypeArgumentType.class, ConstantArgumentSerializer.of((ignored) -> ItemActionTypeArgumentType.action()));
        ArgumentTypeRegistry.registerArgumentType(RA_Additions.identifier("bientity_action_type_layer"), BiEntityActionTypeArgumentType.class, ConstantArgumentSerializer.of((ignored) -> BiEntityActionTypeArgumentType.action()));
        ArgumentTypeRegistry.registerArgumentType(RA_Additions.identifier("power_tag_layer"), PowerTypeArgumentType.class, ConstantArgumentSerializer.of((test) -> PowerTypeArgumentType.power()));

        registerResourceListeners();
    }

    public void registerResourceListeners() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ActionTypes());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ConditionTypes());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(EntityActionTagManager.ACTION_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BlockActionTagManager.ACTION_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ItemActionTagManager.ACTION_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BiEntityActionTagManager.ACTION_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(EntityConditionTagManager.CONDITION_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BlockConditionTagManager.CONDITION_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ItemConditionTagManager.CONDITION_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BiEntityConditionTagManager.CONDITION_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(PowerTagManager.POWER_TAG_LOADER);
    }
}
