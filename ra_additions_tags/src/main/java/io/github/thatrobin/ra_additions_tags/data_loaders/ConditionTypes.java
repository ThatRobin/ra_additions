package io.github.thatrobin.ra_additions_tags.data_loaders;

import com.google.gson.*;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.NamespaceAlias;
import io.github.apace100.calio.data.MultiJsonDataLoader;
import io.github.apace100.calio.data.SerializableData;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_tags.registries.EntityConditionRegistry;
import io.github.thatrobin.ra_additions_tags.registries.ItemConditionRegistry;
import io.github.thatrobin.ra_additions_tags.registries.BiEntityConditionRegistry;
import io.github.thatrobin.ra_additions_tags.registries.BlockConditionRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class ConditionTypes extends MultiJsonDataLoader implements IdentifiableResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public ConditionTypes() {
        super(GSON, "conditions");
    }

    @Override
    protected void apply(Map<Identifier, List<JsonElement>> loader, ResourceManager manager, Profiler profiler) {
        EntityConditionRegistry.reset();
        BlockConditionRegistry.reset();
        BiEntityConditionRegistry.reset();
        ItemConditionRegistry.reset();
        loader.forEach((id, jel) -> {
            for (JsonElement je : jel) {
                try {
                    SerializableData.CURRENT_NAMESPACE = id.getNamespace();
                    SerializableData.CURRENT_PATH = id.getPath();
                    String namespace = id.getNamespace();
                    if(id.getPath().startsWith("entity/")) {
                        Identifier newID = new Identifier(namespace, id.getPath().substring(7));
                        readEntityCondition(newID, je, ConditionType::new);
                    } else if(id.getPath().startsWith("block/")) {
                        Identifier newID = new Identifier(namespace, id.getPath().substring(6));
                        readBlockCondition(newID, je, ConditionType::new);
                    } else if(id.getPath().startsWith("item/")) {
                        Identifier newID = new Identifier(namespace, id.getPath().substring(5));
                        readItemCondition(newID, je, ConditionType::new);
                    } else if(id.getPath().startsWith("bientity/")) {
                        Identifier newID = new Identifier(namespace, id.getPath().substring(9));
                        readBiEntityCondition(newID, je, ConditionType::new);
                    }

                } catch (Exception e) {
                    Apoli.LOGGER.error("There was a problem reading condition file " + id.toString() + " (skipping): " + e.getMessage());
                }
            }
        });
        SerializableData.CURRENT_NAMESPACE = null;
        SerializableData.CURRENT_PATH = null;
        Apoli.LOGGER.info("Finished loading conditions from data files. Registry contains " + EntityConditionRegistry.size() + " conditions.");
    }

    private void readEntityCondition(Identifier id, JsonElement je, BiFunction<Identifier, ConditionFactory<?>.Instance, ConditionType> conditionTypeFactory) {
        JsonObject jo = je.getAsJsonObject();
        Identifier factoryId = Identifier.tryParse(JsonHelper.getString(jo, "type"));
        Optional<ConditionFactory<Entity>> optionalFactory = ApoliRegistries.ENTITY_CONDITION.getOrEmpty(factoryId);
        if (optionalFactory.isEmpty()) {
            if (factoryId != null && NamespaceAlias.hasAlias(factoryId)) {
                optionalFactory = ApoliRegistries.ENTITY_CONDITION.getOrEmpty(NamespaceAlias.resolveAlias(factoryId));
            }
            if (optionalFactory.isEmpty()) {
                if (factoryId != null) {
                    throw new JsonSyntaxException("Condition type \"" + factoryId + "\" is not defined.");
                }
            }
        }
        if (optionalFactory.isPresent()) {
            ConditionFactory<Entity>.Instance factoryInstance = optionalFactory.get().read(jo);
            ConditionType type = conditionTypeFactory.apply(id, factoryInstance);
            if (!EntityConditionRegistry.contains(id)) {
                EntityConditionRegistry.register(id, type);
            } else {
                EntityConditionRegistry.update(id, type);
            }
        }
    }

    private void readBlockCondition(Identifier id, JsonElement je, BiFunction<Identifier, ConditionFactory<?>.Instance, ConditionType> conditionTypeFactory) {
        JsonObject jo = je.getAsJsonObject();
        Identifier factoryId = Identifier.tryParse(JsonHelper.getString(jo, "type"));
        Optional<ConditionFactory<CachedBlockPosition>> optionalFactory = ApoliRegistries.BLOCK_CONDITION.getOrEmpty(factoryId);
        if(optionalFactory.isEmpty()) {
            if (factoryId != null && NamespaceAlias.hasAlias(factoryId)) {
                optionalFactory = ApoliRegistries.BLOCK_CONDITION.getOrEmpty(NamespaceAlias.resolveAlias(factoryId));
            }
            if(optionalFactory.isEmpty()) {
                throw new JsonSyntaxException("Condition type \"" + factoryId + "\" is not defined.");
            }
        }
        ConditionFactory<CachedBlockPosition>.Instance factoryInstance = optionalFactory.get().read(jo);
        ConditionType type = conditionTypeFactory.apply(id, factoryInstance);
        if(!BlockConditionRegistry.contains(id)) {
            BlockConditionRegistry.register(id, type);
        } else {
            BlockConditionRegistry.update(id, type);
        }
    }

    private void readItemCondition(Identifier id, JsonElement je, BiFunction<Identifier, ConditionFactory<?>.Instance, ConditionType> conditionTypeFactory) {
        JsonObject jo = je.getAsJsonObject();
        Identifier factoryId = Identifier.tryParse(JsonHelper.getString(jo, "type"));
        Optional<ConditionFactory<ItemStack>> optionalFactory = ApoliRegistries.ITEM_CONDITION.getOrEmpty(factoryId);
        if(optionalFactory.isEmpty()) {
            if (factoryId != null && NamespaceAlias.hasAlias(factoryId)) {
                optionalFactory = ApoliRegistries.ITEM_CONDITION.getOrEmpty(NamespaceAlias.resolveAlias(factoryId));
            }
            if(optionalFactory.isEmpty()) {
                throw new JsonSyntaxException("Condition type \"" + factoryId + "\" is not defined.");
            }
        }
        ConditionFactory<ItemStack>.Instance factoryInstance = optionalFactory.get().read(jo);
        ConditionType type = conditionTypeFactory.apply(id, factoryInstance);
        if(!ItemConditionRegistry.contains(id)) {
            ItemConditionRegistry.register(id, type);
        } else {
            ItemConditionRegistry.update(id, type);
        }
    }

    private void readBiEntityCondition(Identifier id, JsonElement je, BiFunction<Identifier, ConditionFactory<?>.Instance, ConditionType> conditionTypeFactory) {
        JsonObject jo = je.getAsJsonObject();
        Identifier factoryId = Identifier.tryParse(JsonHelper.getString(jo, "type"));
        Optional<ConditionFactory<Pair<Entity, Entity>>> optionalFactory = ApoliRegistries.BIENTITY_CONDITION.getOrEmpty(factoryId);
        if(optionalFactory.isEmpty()) {
            if (factoryId != null && NamespaceAlias.hasAlias(factoryId)) {
                optionalFactory = ApoliRegistries.BIENTITY_CONDITION.getOrEmpty(NamespaceAlias.resolveAlias(factoryId));
            }
            if(optionalFactory.isEmpty()) {
                throw new JsonSyntaxException("Condition type \"" + factoryId + "\" is not defined.");
            }
        }
        ConditionFactory<Pair<Entity, Entity>>.Instance factoryInstance = optionalFactory.get().read(jo);
        ConditionType type = conditionTypeFactory.apply(id, factoryInstance);
        if(!BiEntityConditionRegistry.contains(id)) {
            BiEntityConditionRegistry.register(id, type);
        } else {
            BiEntityConditionRegistry.update(id, type);
        }
    }

    @Override
    public Identifier getFabricId() {
        return RA_Additions.identifier("conditions");
    }

}
