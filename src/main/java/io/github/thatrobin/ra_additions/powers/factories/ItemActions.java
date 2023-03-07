package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.docky.utils.SectionTitleManager;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;

public class ItemActions {

    public static void register(String label) {
        SectionTitleManager.put("Action Types", "item_action");

        register(new ActionFactory<>(RA_Additions.identifier("modify_item"), new SerializableDataExt(label)
                .add("modifier", "The Identifier of an item modifier.", SerializableDataTypes.IDENTIFIER),
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
                }), "Applies an item modifier to the item stack with a player fulfilling the \"this\" criteria.");

        register(new ActionFactory<>(RA_Additions.identifier("execute_action"), new SerializableDataExt(label)
                .add("item_action", "The Identifier of the tag or action file to be executed", SerializableDataTypes.STRING),
                (data, itemStackPair) -> {
                    String idStr = data.getString("item_action");
                    if(idStr.startsWith("#")) {
                        Identifier id = Identifier.tryParse(idStr.substring(1));
                        Collection<ActionType> actions = ItemActionTagManager.ACTION_TAG_LOADER.getTag(id);
                        for (ActionType action : actions) {
                            action.getAction().accept(itemStackPair);
                        }
                    } else {
                        Identifier id = Identifier.tryParse(idStr);
                        ActionFactory<Pair<World,ItemStack>>.Instance action =  ItemActionRegistry.get(id).getAction();
                        action.accept(itemStackPair);
                    }
                }), "Executes an item action that is stored in a file.");
    }

    private static void register(ActionFactory<Pair<World, ItemStack>> factory, String description) {
        //DockyRegistry.register(factory, "item_action", description,
        //        "C:\\Users\\robin\\IdeaProjects\\ra_additions\\run\\saves\\New World\\datapacks\\Test Pack\\data\\test_pack\\actions\\item\\" + factory.getSerializerId().getPath() + "_example.json");
        Registry.register(ApoliRegistries.ITEM_ACTION, factory.getSerializerId(), factory);
    }

}
