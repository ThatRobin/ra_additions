package io.github.thatrobin.ra_additions_tags.factories;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_tags.data_loaders.ActionType;
import io.github.thatrobin.ra_additions_tags.data_loaders.EntityActionTagManager;
import io.github.thatrobin.ra_additions_tags.registries.EntityActionRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class EntityActions {

    public static void register() {
        register(new ActionFactory<>(RA_Additions.identifier("execute_action"), new SerializableDataExt()
                .add("entity_action", "The Identifier of the tag or action file to be executed", SerializableDataTypes.STRING),
                (data, entity) -> {
                    String idStr = data.getString("entity_action");
                    if(idStr.startsWith("#")) {
                        Identifier id = Identifier.tryParse(idStr.substring(1));
                        Collection<ActionType> actions = EntityActionTagManager.ACTION_TAG_LOADER.getTag(id);
                        for (ActionType action : actions) {
                            action.getAction().accept(entity);
                        }
                    } else {
                        Identifier id = Identifier.tryParse(idStr);
                        ActionFactory<Entity>.Instance action =  EntityActionRegistry.get(id).getAction();
                        action.accept(entity);
                    }
                }), "Executes an entity action that is stored in a file.");
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
