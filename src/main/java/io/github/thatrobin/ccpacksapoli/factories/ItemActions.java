package io.github.thatrobin.ccpacksapoli.factories;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.access.MutableItemStack;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ccpacksapoli.CCPacksApoli;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ItemActions {

    public static void register() {
        register(new ActionFactory<>(CCPacksApoli.identifier("modify_item"), new SerializableData()
                .add("modifier", SerializableDataTypes.IDENTIFIER),
                (data, worldAndStack) -> {
                    MinecraftServer server = worldAndStack.getLeft().getServer();
                    if(server != null) {
                        Identifier id = data.getId("modifier");
                        LootFunctionManager lootFunctionManager = server.getItemModifierManager();
                        LootFunction lootFunction = lootFunctionManager.get(id);
                        if (lootFunction == null) {
                            Apoli.LOGGER.info("Unknown item modifier used in `modify` action: " + id);
                            return;
                        }
                        ServerWorld serverWorld = server.getOverworld();

                        ItemStack stack = worldAndStack.getRight();
                        LootContext.Builder builder = (new LootContext.Builder(serverWorld)).parameter(LootContextParameters.ORIGIN, new Vec3d(0, 0, 0)).optionalParameter(LootContextParameters.THIS_ENTITY, serverWorld.getRandomAlivePlayer());
                        ItemStack newStack = lootFunction.apply(stack, builder.build(LootContextTypes.COMMAND));
                        stack = newStack;
                        stack.setCount(newStack.getCount());
                        stack.setNbt(newStack.getNbt());
                    }
                }));
    }

    private static void register(ActionFactory<Pair<World, ItemStack>> conditionFactory) {
        Registry.register(ApoliRegistries.ITEM_ACTION, conditionFactory.getSerializerId(), conditionFactory);
    }

}
