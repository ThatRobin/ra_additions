package io.github.thatrobin.ra_additions_tags.factories;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_tags.data_loaders.ActionType;
import io.github.thatrobin.ra_additions_tags.data_loaders.BiEntityActionTagManager;
import io.github.thatrobin.ra_additions_tags.registries.BiEntityActionRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Collection;

public class BiEntityActions {

    public static void register() {
        register(new ActionFactory<>(RA_Additions.identifier("execute_action"), new SerializableDataExt()
                .add("bientity_action", "The Identifier of the tag or action file to be executed", SerializableDataTypes.STRING),
                (data, entities) -> {
                    String idStr = data.getString("bientity_action");
                    if(idStr.startsWith("#")) {
                        Identifier id = Identifier.tryParse(idStr.substring(1));
                        Collection<ActionType> actions = BiEntityActionTagManager.ACTION_TAG_LOADER.getTag(id);
                        for (ActionType action : actions) {
                            action.getAction().accept(entities);
                        }
                    } else {
                        Identifier id = Identifier.tryParse(idStr);
                        ActionFactory<Pair<Entity,Entity>>.Instance action =  BiEntityActionRegistry.get(id).getAction();
                        action.accept(entities);
                    }
                }), "Executes a bi-entity action that is stored in a file.");
    }

    private static void register(ActionFactory<Pair<Entity,Entity>> factory, String description) {
        DockyEntry entry = new DockyEntry()
                .setHeader("Action Types")
                .setFactory(factory)
                .setDescription(description)
                .setType("bientity_action_types");
        if(RA_Additions.getExamplePathRoot() != null) entry.setExamplePath(RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\actions\\bientity\\" + factory.getSerializerId().getPath() + "_example.json");
        DockyRegistry.register(entry);
        Registry.register(ApoliRegistries.BIENTITY_ACTION, factory.getSerializerId(), factory);
    }
}
