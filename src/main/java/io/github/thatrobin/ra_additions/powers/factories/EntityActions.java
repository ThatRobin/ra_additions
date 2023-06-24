package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.powers.BrewingStandPower;
import io.github.thatrobin.ra_additions.powers.FurnacePower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;

public class EntityActions {

    public static void register() {
        register(new ActionFactory<>(RA_Additions.identifier("use_internal_block"), new SerializableDataExt()
                .add("block_power", "The Identifier of the choice layer that the action will open.", ApoliDataTypes.POWER_TYPE),
                (data, entity) -> {
                    PowerType<?> powerType = data.get("block_power");
                    Power power = PowerHolderComponent.KEY.get(entity).getPower(powerType);
                    if(entity instanceof PlayerEntity player) {
                        if(power instanceof FurnacePower furnacePower) {
                            player.openHandledScreen(furnacePower);
                        }
                        if(power instanceof BrewingStandPower brewingStandPower) {
                            player.openHandledScreen(brewingStandPower);
                        }
                    }
                }
        ), "Opens the specified choice screen for the player it is executed as.");
        register(new ActionFactory<>(RA_Additions.identifier("sleep"), new SerializableDataExt(),
                (data, entity) -> {
                    if(entity instanceof PlayerEntity player) {
                        player.sleep(player.getBlockPos());
                        player.sleepTimer = 0;
                        if (!player.world.isClient) {
                            ((ServerWorld) player.world).updateSleepingPlayers();
                        }
                    }
                }
        ), "Opens the specified choice screen for the player it is executed as.");
    }

    private static void register(ActionFactory<Entity> factory, String description) {
        DockyEntry entry = new DockyEntry()
                .setHeader("Action Types")
                .setFactory(factory)
                .setDescription(description)
                .setType("entity_action_types");
        if(RA_Additions.getExamplePathRoot() != null) entry.setExamplePath(RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\actions\\entity\\" + factory.getSerializerId().getPath() + "_example.json");
        DockyRegistry.register(entry);
        Registry.register(ApoliRegistries.ENTITY_ACTION, factory.getSerializerId(), factory);
    }
}
