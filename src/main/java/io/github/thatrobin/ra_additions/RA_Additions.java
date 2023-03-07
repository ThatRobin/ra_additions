package io.github.thatrobin.ra_additions;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.util.NamespaceAlias;
import io.github.thatrobin.ra_additions.choice.Choice;
import io.github.thatrobin.ra_additions.choice.ChoiceLayers;
import io.github.thatrobin.ra_additions.choice.ChoiceManager;
import io.github.thatrobin.ra_additions.commands.*;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactories;
import io.github.thatrobin.ra_additions.goals.factories.GoalTypes;
import io.github.thatrobin.ra_additions.networking.RAA_ModPacketC2S;
import io.github.thatrobin.ra_additions.powers.BorderPower;
import io.github.thatrobin.ra_additions.powers.factories.*;
import io.github.thatrobin.ra_additions.util.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RA_Additions implements ModInitializer {

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
        Choice.init();

        NamespaceAlias.addAlias(MODID, "apoli");
        NamespaceAlias.addAlias("origins", "apoli");

        RAA_ClassDataRegistry.registerAll();

        GoalFactories.register("tasks");
        PowerFactories.register("powers");
        EntityConditions.register("entity_conditions");
        EntityActions.register("entity_actions");
        BiEntityConditions.register("bientity_conditions");
        BiEntityActions.register("bientity_actions");
        BlockConditions.register("block_conditions");
        BlockActions.register("block_actions");
        ItemConditions.register("item_conditions");
        ItemActions.register("item_actions");

        RAA_ModPacketC2S.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> {
            ExecuteCommandExtention.register(dispatcher);
            ChoiceCommand.register(dispatcher);
            RAAPowerCommand.register(dispatcher);
            RAAActionCommand.register(dispatcher);
        });

        WorldRenderEvents.LAST.register(identifier("render_border"), (context) -> {
            if (MinecraftClient.getInstance().world != null) {
                Iterable<Entity> entities = MinecraftClient.getInstance().world.getEntities();
                for(Entity entity : entities) {
                    if(entity instanceof LivingEntity livingEntity) {
                        PowerHolderComponent component = PowerHolderComponent.KEY.get(livingEntity);
                        for (BorderPower power : component.getPowers(BorderPower.class)) {
                            RenderBorderPower.renderWorldBorder(context.camera(), power);
                        }
                    }
                }
            }
        });

        registerResourceListeners();
        RAAEntitySelectorOptions.register();

        ArgumentTypeRegistry.registerArgumentType(identifier("choice_layer"), LayerArgument.class, ConstantArgumentSerializer.of((test) -> LayerArgument.layer()));
        ArgumentTypeRegistry.registerArgumentType(identifier("power_tag_layer"), PowerTypeArgumentType.class, ConstantArgumentSerializer.of((test) -> PowerTypeArgumentType.power()));
        ArgumentTypeRegistry.registerArgumentType(identifier("entity_action_type_layer"), EntityActionTypeArgumentType.class, ConstantArgumentSerializer.of((test) -> EntityActionTypeArgumentType.action()));
        ArgumentTypeRegistry.registerArgumentType(identifier("block_action_type_layer"), BlockActionTypeArgumentType.class, ConstantArgumentSerializer.of((test) -> BlockActionTypeArgumentType.action()));
        ArgumentTypeRegistry.registerArgumentType(identifier("item_action_type_layer"), ItemActionTypeArgumentType.class, ConstantArgumentSerializer.of((test) -> ItemActionTypeArgumentType.action()));
        ArgumentTypeRegistry.registerArgumentType(identifier("bientity_action_type_layer"), BiEntityActionTypeArgumentType.class, ConstantArgumentSerializer.of((test) -> BiEntityActionTypeArgumentType.action()));

        ServerWorldEvents.UNLOAD.register(((server, world) -> KeybindRegistry.clear()));
    }

    public void registerResourceListeners() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ActionTypes());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ConditionTypes());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(EntityActionTagManager.ACTION_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BlockActionTagManager.ACTION_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ItemActionTagManager.ACTION_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BiEntityActionTagManager.ACTION_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(PowerTagManager.POWER_TAG_LOADER);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new UniversalPowerManager());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ChoiceManager());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ChoiceLayers());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new GoalTypes());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new KeybindManager());
    }

    public static Identifier identifier(String path) {
        return new Identifier("ra_additions", path);
    }

}
