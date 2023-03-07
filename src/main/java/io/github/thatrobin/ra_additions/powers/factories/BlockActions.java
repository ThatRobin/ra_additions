package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.docky.utils.SectionTitleManager;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.util.*;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;

public class BlockActions {

    public static void register(String label) {
        SectionTitleManager.put("Action Types", "block_action");

        register(new ActionFactory<>(RA_Additions.identifier("execute_action"), new SerializableDataExt(label)
                .add("block_action", "The Identifier of the tag or action file to be executed", SerializableDataTypes.STRING),
                (data, itemStackPair) -> {
                    String idStr = data.getString("block_action");
                    if(idStr.startsWith("#")) {
                        Identifier id = Identifier.tryParse(idStr.substring(1));
                        Collection<ActionType> actions = BlockActionTagManager.ACTION_TAG_LOADER.getTag(id);
                        for (ActionType action : actions) {
                            action.getAction().accept(itemStackPair);
                        }
                    } else {
                        Identifier id = Identifier.tryParse(idStr);
                        ActionFactory<Triple<World, BlockPos, Direction>>.Instance action =  BlockActionRegistry.get(id).getAction();
                        action.accept(itemStackPair);
                    }
                }));
    }

    private static void register(ActionFactory<Triple<World, BlockPos, Direction>> factory) {
        Registry.register(ApoliRegistries.BLOCK_ACTION, factory.getSerializerId(), factory);
    }

}
