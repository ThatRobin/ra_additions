package io.github.thatrobin.ra_additions_tags.factories;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_tags.data_loaders.BiEntityConditionTagManager;
import io.github.thatrobin.ra_additions_tags.data_loaders.ConditionType;
import io.github.thatrobin.ra_additions_tags.registries.BiEntityConditionRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Collection;

public class BiEntityConditions {

    public static void register() {
        register(new ConditionFactory<>(RA_Additions.identifier("evaluate_condition"), new SerializableDataExt()
                .add("bientity_condition", "The Identifier of the tag or condition file to be evaluated", SerializableDataTypes.STRING),
                (data, entities) -> {
                    String idStr = data.getString("bientity_condition");
                    if(idStr.startsWith("#")) {
                        Identifier id = Identifier.tryParse(idStr.substring(1));
                        Collection<ConditionType> conditions = BiEntityConditionTagManager.CONDITION_TAG_LOADER.getTag(id);
                        boolean result = true;
                        for (ConditionType condition : conditions) {
                            if(!condition.getCondition().test(entities)) {
                                result = false;
                            }
                        }
                        return result;
                    } else {
                        Identifier id = Identifier.tryParse(idStr);
                        ConditionFactory<Pair<Entity,Entity>>.Instance condition =  BiEntityConditionRegistry.get(id).getCondition();
                        return condition.test(entities);
                    }
                }), "Evaluates a bi-entity condition that is stored in a file.");
    }

    @SuppressWarnings("SameParameterValue")
    private static void register(ConditionFactory<Pair<Entity,Entity>> factory, String description) {
        DockyEntry entry = new DockyEntry()
                .setHeader("Condition Types")
                .setFactory(factory)
                .setDescription(description)
                .setType("bientity_condition_types");
        if(RA_Additions.getExamplePathRoot() != null) entry.setExamplePath(RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\conditions\\bientity\\" + factory.getSerializerId().getPath() + "_example.json");
        DockyRegistry.register(entry);
        Registry.register(ApoliRegistries.BIENTITY_CONDITION, factory.getSerializerId(), factory);
    }
}
