package io.github.thatrobin.ra_additions_tags.factories;


import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_tags.data_loaders.BlockConditionTagManager;
import io.github.thatrobin.ra_additions_tags.data_loaders.ConditionType;
import io.github.thatrobin.ra_additions_tags.registries.BlockConditionRegistry;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class BlockConditions {

    public static void register() {
        register(new ConditionFactory<>(RA_Additions.identifier("evaluate_condition"), new SerializableDataExt()
                .add("block_condition", "The Identifier of the tag or condition file to be evaluated", SerializableDataTypes.STRING),
                (data, block) -> {
                    String idStr = data.getString("block_condition");
                    if(idStr.startsWith("#")) {
                        Identifier id = Identifier.tryParse(idStr.substring(1));
                        Collection<ConditionType> conditions = BlockConditionTagManager.CONDITION_TAG_LOADER.getTag(id);
                        boolean result = true;
                        for (ConditionType condition : conditions) {
                            if(!condition.getCondition().test(block)) {
                                result = false;
                            }
                        }
                        return result;
                    } else {
                        Identifier id = Identifier.tryParse(idStr);
                        ConditionFactory<CachedBlockPosition>.Instance condition =  BlockConditionRegistry.get(id).getCondition();
                        return condition.test(block);
                    }
                }), "Evaluates a block condition that is stored in a file.");
    }

    @SuppressWarnings("SameParameterValue")
    private static void register(ConditionFactory<CachedBlockPosition> factory, String description) {
        DockyEntry entry = new DockyEntry()
                .setHeader("Condition Types")
                .setFactory(factory)
                .setDescription(description)
                .setType("block_condition_types");
        if(RA_Additions.getExamplePathRoot() != null) entry.setExamplePath(RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\conditions\\block\\" + factory.getSerializerId().getPath() + "_example.json");
        DockyRegistry.register(entry);
        Registry.register(ApoliRegistries.BLOCK_CONDITION, factory.getSerializerId(), factory);
    }
}
