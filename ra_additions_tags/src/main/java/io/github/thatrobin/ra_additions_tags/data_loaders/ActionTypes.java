package io.github.thatrobin.ra_additions_tags.data_loaders;

import com.google.gson.*;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.NamespaceAlias;
import io.github.apace100.calio.data.MultiJsonDataLoader;
import io.github.apace100.calio.data.SerializableData;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_tags.registries.BiEntityActionRegistry;
import io.github.thatrobin.ra_additions_tags.registries.BlockActionRegistry;
import io.github.thatrobin.ra_additions_tags.registries.EntityActionRegistry;
import io.github.thatrobin.ra_additions_tags.registries.ItemActionRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.function.BiFunction;

public class ActionTypes extends MultiJsonDataLoader implements IdentifiableResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public ActionTypes() {
        super(GSON, "actions");
    }

    @Override
    protected void apply(Map<Identifier, List<JsonElement>> loader, ResourceManager manager, Profiler profiler) {
        EntityActionRegistry.reset();
        BlockActionRegistry.reset();
        BiEntityActionRegistry.reset();
        ItemActionRegistry.reset();
        loader.forEach((id, jel) -> {
            for (JsonElement je : jel) {
                try {
                    SerializableData.CURRENT_NAMESPACE = id.getNamespace();
                    SerializableData.CURRENT_PATH = id.getPath();
                    String namespace = id.getNamespace();
                    if(id.getPath().startsWith("entity/")) {
                        Identifier newID = new Identifier(namespace, id.getPath().substring(7));
                        readEntityAction(newID, je, ActionType::new);
                    } else if(id.getPath().startsWith("block/")) {
                        Identifier newID = new Identifier(namespace, id.getPath().substring(6));
                        readBlockAction(newID, je, ActionType::new);
                    } else if(id.getPath().startsWith("item/")) {
                        Identifier newID = new Identifier(namespace, id.getPath().substring(5));
                        readItemAction(newID, je, ActionType::new);
                    } else if(id.getPath().startsWith("bientity/")) {
                        Identifier newID = new Identifier(namespace, id.getPath().substring(9));
                        readBiEntityAction(newID, je, ActionType::new);
                    }

                } catch (Exception e) {
                    Apoli.LOGGER.error("There was a problem reading action file " + id.toString() + " (skipping): " + e.getMessage());
                }
            }
        });
        SerializableData.CURRENT_NAMESPACE = null;
        SerializableData.CURRENT_PATH = null;
        Apoli.LOGGER.info("Finished loading actions from data files. Registry contains " + EntityActionRegistry.size() + " actions.");
    }

    private void readEntityAction(Identifier id, JsonElement je, BiFunction<Identifier, ActionFactory<?>.Instance, ActionType> actionTypeFactory) {
        JsonObject jo = je.getAsJsonObject();
        Identifier factoryId = Identifier.tryParse(JsonHelper.getString(jo, "type"));
        Optional<ActionFactory<Entity>> optionalFactory = ApoliRegistries.ENTITY_ACTION.getOrEmpty(factoryId);
        if(optionalFactory.isEmpty()) {
            if (factoryId != null && NamespaceAlias.hasAlias(factoryId)) {
                optionalFactory = ApoliRegistries.ENTITY_ACTION.getOrEmpty(NamespaceAlias.resolveAlias(factoryId));
            }
            if(optionalFactory.isEmpty()) {
                if (factoryId != null) {
                    throw new JsonSyntaxException("Action type \"" + factoryId + "\" is not defined.");
                }
            }
        }
        if(optionalFactory.isPresent()) {
            ActionFactory<Entity>.Instance factoryInstance = optionalFactory.get().read(jo);
            ActionType type = actionTypeFactory.apply(id, factoryInstance);
            if (!EntityActionRegistry.contains(id)) {
                EntityActionRegistry.register(id, type);
            } else {
                EntityActionRegistry.update(id, type);
            }
        }
    }

    private void readBlockAction(Identifier id, JsonElement je, BiFunction<Identifier, ActionFactory<?>.Instance, ActionType> actionTypeFactory) {
        JsonObject jo = je.getAsJsonObject();
        Identifier factoryId = Identifier.tryParse(JsonHelper.getString(jo, "type"));
        Optional<ActionFactory<Triple<World, BlockPos, Direction>>> optionalFactory = ApoliRegistries.BLOCK_ACTION.getOrEmpty(factoryId);
        if(optionalFactory.isEmpty()) {
            if (factoryId != null && NamespaceAlias.hasAlias(factoryId)) {
                optionalFactory = ApoliRegistries.BLOCK_ACTION.getOrEmpty(NamespaceAlias.resolveAlias(factoryId));
            }
            if(optionalFactory.isEmpty()) {
                if (factoryId != null) {
                    throw new JsonSyntaxException("Action type \"" + factoryId + "\" is not defined.");
                }
            }
        }
        if(optionalFactory.isPresent()) {
            ActionFactory<Triple<World, BlockPos, Direction>>.Instance factoryInstance = optionalFactory.get().read(jo);
            ActionType type = actionTypeFactory.apply(id, factoryInstance);
            if (!BlockActionRegistry.contains(id)) {
                BlockActionRegistry.register(id, type);
            } else {
                BlockActionRegistry.update(id, type);
            }
        }
    }

    private void readItemAction(Identifier id, JsonElement je, BiFunction<Identifier, ActionFactory<?>.Instance, ActionType> actionTypeFactory) {
        JsonObject jo = je.getAsJsonObject();
        Identifier factoryId = Identifier.tryParse(JsonHelper.getString(jo, "type"));
        Optional<ActionFactory<Pair<World, ItemStack>>> optionalFactory = ApoliRegistries.ITEM_ACTION.getOrEmpty(factoryId);
        if(optionalFactory.isEmpty()) {
            if (factoryId != null && NamespaceAlias.hasAlias(factoryId)) {
                optionalFactory = ApoliRegistries.ITEM_ACTION.getOrEmpty(NamespaceAlias.resolveAlias(factoryId));
            }
            if(optionalFactory.isEmpty()) {
                if (factoryId != null) {
                    throw new JsonSyntaxException("Action type \"" + factoryId + "\" is not defined.");
                }
            }
        }
        if(optionalFactory.isPresent()) {
            ActionFactory<Pair<World, ItemStack>>.Instance factoryInstance = optionalFactory.get().read(jo);
            ActionType type = actionTypeFactory.apply(id, factoryInstance);
            if (!ItemActionRegistry.contains(id)) {
                ItemActionRegistry.register(id, type);
            } else {
                ItemActionRegistry.update(id, type);
            }
        }
    }

    private void readBiEntityAction(Identifier id, JsonElement je, BiFunction<Identifier, ActionFactory<?>.Instance, ActionType> actionTypeFactory) {
        JsonObject jo = je.getAsJsonObject();
        Identifier factoryId = Identifier.tryParse(JsonHelper.getString(jo, "type"));
        Optional<ActionFactory<Pair<Entity, Entity>>> optionalFactory = ApoliRegistries.BIENTITY_ACTION.getOrEmpty(factoryId);
        if(optionalFactory.isEmpty()) {
            if (factoryId != null && NamespaceAlias.hasAlias(factoryId)) {
                optionalFactory = ApoliRegistries.BIENTITY_ACTION.getOrEmpty(NamespaceAlias.resolveAlias(factoryId));
            }
            if(optionalFactory.isEmpty()) {
                if (factoryId != null) {
                    throw new JsonSyntaxException("Action type \"" + factoryId + "\" is not defined.");
                }
            }
        }
        if(optionalFactory.isPresent()) {
            ActionFactory<Pair<Entity, Entity>>.Instance factoryInstance = optionalFactory.get().read(jo);
            ActionType type = actionTypeFactory.apply(id, factoryInstance);
            if (!BiEntityActionRegistry.contains(id)) {
                BiEntityActionRegistry.register(id, type);
            } else {
                BiEntityActionRegistry.update(id, type);
            }
        }
    }

    @Override
    public Identifier getFabricId() {
        return RA_Additions.identifier("actions");
    }



}
