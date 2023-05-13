package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.action.ActionType;
import io.github.apace100.apoli.power.factory.condition.ConditionType;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.util.BlockActionRegistry;
import io.github.thatrobin.ra_additions.util.BlockActionTagManager;
import io.github.thatrobin.ra_additions.util.RAA_Registries;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registry;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;

public class EntityBlockActions {

    public static ActionType<Pair<Entity, Triple<World, BlockPos, Direction>>> ENTITYBLOCK = new ActionType<>("EntityBlockCondition", RAA_Registries.ENTITYBLOCK_ACTION);


    public static void register() {
        register(new ActionFactory<>(RA_Additions.identifier("mimic_item_on_block"), new SerializableDataExt()
                .add("item_stack", "The stack to be used for the action", SerializableDataTypes.ITEM_STACK)
                .add("hand", "The hand for the action to use", SerializableDataTypes.HAND, Hand.MAIN_HAND),
                (data, entityTriplePair) -> {
                    ItemStack stack = data.get("item_stack");
                    Hand hand = data.get("hand");
                    Triple<World, BlockPos, Direction> triple = entityTriplePair.getRight();
                    BlockHitResult hitResult = new BlockHitResult(Vec3d.ofCenter(triple.getMiddle()), triple.getRight(), triple.getMiddle(), true);
                    if(entityTriplePair.getLeft() instanceof PlayerEntity player) {
                        stack.useOnBlock(new ItemUsageContext(player, hand, hitResult));
                    }
                }), "Executes a block action that is stored in a file.");
    }

    @SuppressWarnings("SameParameterValue")
    private static void register(ActionFactory<Pair<Entity, Triple<World, BlockPos, Direction>>> factory, String description) {
        DockyEntry entry = new DockyEntry()
                .setHeader("Action Types")
                .setFactory(factory)
                .setDescription(description)
                .setType("block_action_types");
        if(RA_Additions.getExamplePathRoot() != null) entry.setExamplePath(RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\actions\\block\\" + factory.getSerializerId().getPath() + "_example.json");
        DockyRegistry.register(entry);
        Registry.register(RAA_Registries.ENTITYBLOCK_ACTION, factory.getSerializerId(), factory);
    }

}
