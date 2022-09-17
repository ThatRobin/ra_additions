package io.github.thatrobin.ra_additions;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.util.NamespaceAlias;
import io.github.apace100.calio.util.OrderedResourceListeners;
import io.github.thatrobin.ra_additions.choice.Choice;
import io.github.thatrobin.ra_additions.choice.ChoiceLayers;
import io.github.thatrobin.ra_additions.choice.ChoiceManager;
import io.github.thatrobin.ra_additions.commands.ChoiceCommand;
import io.github.thatrobin.ra_additions.commands.LayerArgument;
import io.github.thatrobin.ra_additions.networking.RAA_ModPacketC2S;
import io.github.thatrobin.ra_additions.powers.BorderPower;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactories;
import io.github.thatrobin.ra_additions.goals.factories.GoalTypes;
import io.github.thatrobin.ra_additions.powers.factories.*;
import io.github.thatrobin.ra_additions.util.RAA_ClassDataRegistry;
import io.github.thatrobin.ra_additions.util.RenderBorderPower;
import io.github.thatrobin.ra_additions.util.UniversalPowerManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RA_Additions implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger(RA_Additions.class);
    public static int[] SEMVER;
    public static String MODID = "ra_additions";
    public static String VERSION = "";

    @SuppressWarnings("deprecation")
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

        GoalFactories.register();
        PowerFactories.register();
        EntityConditions.register();
        EntityActions.register();
        BiEntityActions.register();
        ItemConditions.register();
        ItemActions.register();

        RAA_ModPacketC2S.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> ChoiceCommand.register(dispatcher));

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

        OrderedResourceListeners.register(new UniversalPowerManager()).after(new Identifier("apoli","powers")).complete();
        OrderedResourceListeners.register(new ChoiceManager()).before(new Identifier("apoli","powers")).complete();
        OrderedResourceListeners.register(new ChoiceLayers()).before(new Identifier("apoli","powers")).complete();
        OrderedResourceListeners.register(new GoalTypes()).before(new Identifier("apoli","powers")).complete();

        ArgumentTypeRegistry.registerArgumentType(identifier("choice_layer"), LayerArgument.class, ConstantArgumentSerializer.of((test) -> LayerArgument.layer()));


    }

    public static Identifier identifier(String path) {
        return new Identifier("ra_additions", path);
    }

}
