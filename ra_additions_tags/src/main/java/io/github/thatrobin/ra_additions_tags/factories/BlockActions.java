package io.github.thatrobin.ra_additions_tags.factories;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_tags.data_loaders.ActionType;
import io.github.thatrobin.ra_additions_tags.data_loaders.BlockActionTagManager;
import io.github.thatrobin.ra_additions_tags.registries.BlockActionRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;

public class BlockActions {

    public static void register() {
        register(new ActionFactory<>(RA_Additions.identifier("execute_action"), new SerializableDataExt()
                .add("block_action", "The Identifier of the tag or action file to be executed", SerializableDataTypes.STRING),
                (data, blockPosDirectionTriple) -> {
                    String idStr = data.getString("block_action");
                    if(idStr.startsWith("#")) {
                        Identifier id = Identifier.tryParse(idStr.substring(1));
                        Collection<ActionType> actions = BlockActionTagManager.ACTION_TAG_LOADER.getTag(id);
                        for (ActionType action : actions) {
                            action.getAction().accept(blockPosDirectionTriple);
                        }
                    } else {
                        Identifier id = Identifier.tryParse(idStr);
                        ActionFactory<Triple<World, BlockPos, Direction>>.Instance action =  BlockActionRegistry.get(id).getAction();
                        action.accept(blockPosDirectionTriple);
                    }
                }), "Executes a block action that is stored in a file.");
    }

    @SuppressWarnings("SameParameterValue")
    private static void register(ActionFactory<Triple<World, BlockPos, Direction>> factory, String description) {
        DockyEntry entry = new DockyEntry()
                .setHeader("Action Types")
                .setFactory(factory)
                .setDescription(description)
                .setType("block_action_types");
        if(RA_Additions.getExamplePathRoot() != null) entry.setExamplePath(RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\actions\\block\\" + factory.getSerializerId().getPath() + "_example.json");
        DockyRegistry.register(entry);
        Registry.register(ApoliRegistries.BLOCK_ACTION, factory.getSerializerId(), factory);
    }

}
