package io.github.thatrobin.ccpacksapoli.data;

import com.google.gson.JsonParseException;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ccpacksapoli.util.BossBarHudRender;
import io.github.thatrobin.ccpacksapoli.util.BossBarHudRenderOverlay;
import io.github.thatrobin.ccpacksapoli.util.StatBarHudRender;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class CCPacksApoliDataTypes {

    public static final SerializableDataType<StatBarHudRender> STAT_BAR_HUD_RENDER = SerializableDataType.compound(StatBarHudRender.class, new
                    SerializableData()
                    .add("should_render", SerializableDataTypes.BOOLEAN, true)
                    .add("bar_index", SerializableDataTypes.INT, 0)
                    .add("side", SerializableDataTypes.STRING, "right")
                    .add("sprite_location", SerializableDataTypes.IDENTIFIER, new Identifier("ccpacks", "textures/gui/icons.png"))
                    .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
            (dataInst) -> new StatBarHudRender(
                    dataInst.getBoolean("should_render"),
                    dataInst.getInt("bar_index"),
                    dataInst.getId("sprite_location"),
                    dataInst.get("condition"),
                    dataInst.getString("side")),
            (data, inst) -> {
                SerializableData.Instance dataInst = data.new Instance();
                dataInst.set("should_render", inst.shouldRender());
                dataInst.set("bar_index", inst.getBarIndex());
                dataInst.set("sprite_location", inst.getSpriteLocation());
                dataInst.set("condition", inst.getCondition());
                dataInst.set("side", inst.getSide());
                return dataInst;
            });

    public static final SerializableDataType<BossBarHudRenderOverlay> HUD_RENDER_OVERLAY = SerializableDataType.compound(BossBarHudRenderOverlay.class, new
                    SerializableData()
                    .add("should_render", SerializableDataTypes.BOOLEAN, true)
                    .add("bar_index", SerializableDataTypes.INT, 0)
                    .add("priority", SerializableDataTypes.INT, 0)
                    .add("sprite_location", SerializableDataTypes.IDENTIFIER, new Identifier("textures/gui/bars.png"))
                    .add("condition", ApoliDataTypes.ENTITY_CONDITION, null)
                    .add("inverted", SerializableDataTypes.BOOLEAN, false),
            (dataInst) -> new BossBarHudRenderOverlay(
                    dataInst.getBoolean("should_render"),
                    dataInst.getInt("bar_index"),
                    dataInst.getInt("priority"),
                    dataInst.getId("sprite_location"),
                    (ConditionFactory<LivingEntity>.Instance)dataInst.get("condition"),
                    dataInst.getBoolean("inverted")),
            (data, inst) -> {
                SerializableData.Instance dataInst = data.new Instance();
                dataInst.set("should_render", inst.shouldRender());
                dataInst.set("bar_index", inst.getBarIndex());
                dataInst.set("priority", inst.getPriority());
                dataInst.set("sprite_location", inst.getSpriteLocation());
                dataInst.set("condition", inst.getCondition());
                dataInst.set("inverted", inst.isInverted());
                return dataInst;
            });

    public static final SerializableDataType<List<BossBarHudRenderOverlay>> HUD_RENDER_OVERLAYS = SerializableDataType.list(CCPacksApoliDataTypes.HUD_RENDER_OVERLAY);

    public static final SerializableDataType<BossBarHudRender> HUD_RENDER = SerializableDataType.compound(BossBarHudRender.class, new
                    SerializableData()
                    .add("should_render", SerializableDataTypes.BOOLEAN, true)
                    .add("bar_index", SerializableDataTypes.INT, 0)
                    .add("priority", SerializableDataTypes.INT, 0)
                    .add("overlays", CCPacksApoliDataTypes.HUD_RENDER_OVERLAYS, null)
                    .add("sprite_location", SerializableDataTypes.IDENTIFIER, new Identifier("textures/gui/bars.png"))
                    .add("condition", ApoliDataTypes.ENTITY_CONDITION, null)
                    .add("inverted", SerializableDataTypes.BOOLEAN, false),
            (dataInst) -> new BossBarHudRender(
                    dataInst.getBoolean("should_render"),
                    dataInst.getInt("bar_index"),
                    dataInst.getInt("priority"),
                    dataInst.get("overlays"),
                    dataInst.getId("sprite_location"),
                    (ConditionFactory<LivingEntity>.Instance)dataInst.get("condition"),
                    dataInst.getBoolean("inverted")),
            (data, inst) -> {
                SerializableData.Instance dataInst = data.new Instance();
                dataInst.set("should_render", inst.shouldRender());
                dataInst.set("bar_index", inst.getBarIndex());
                dataInst.set("priority", inst.getPriority());
                dataInst.set("overlays", inst.getOverlays());
                dataInst.set("sprite_location", inst.getSpriteLocation());
                dataInst.set("condition", inst.getCondition());
                dataInst.set("inverted", inst.isInverted());
                return dataInst;
            });

    public static final SerializableDataType<List<EntityType<?>>> ENTITY_ENTRY = SerializableDataType.compound(ClassUtil.castClass(List.class),
            new SerializableData()
                    .add("entity", SerializableDataTypes.ENTITY_TYPE, null)
                    .add("tag", SerializableDataTypes.ENTITY_TAG, null),
            dataInstance -> {
                boolean tagPresent = dataInstance.isPresent("tag");
                boolean itemPresent = dataInstance.isPresent("entity");
                if(tagPresent == itemPresent) {
                    throw new JsonParseException("An entity entry is either a tag or an entity, " + (tagPresent ? "not both" : "one has to be provided."));
                }
                if(tagPresent) {
                    TagKey<EntityType<?>> tag = dataInstance.get("tag");
                    var entryList = Registry.ENTITY_TYPE.getEntryList(tag);
                    if (entryList.isPresent()) {
                        return entryList.get().stream().map(RegistryEntry::value).collect(Collectors.toList());
                    } else {
                        return List.of();
                    }
                } else {
                    return List.of((EntityType<?>)dataInstance.get("entity"));
                }
            }, (data, entities) -> {
                SerializableData.Instance inst = data.new Instance();
                if(entities.size() == 1) {
                    inst.set("entity", entities.get(0));
                } else {
                    var itemTags = Registry.ITEM.streamTags();
                    //filter out any tags where the entries do not completely match the items list
                    itemTags = itemTags.filter(tag -> {
                        var entryList = Registry.ITEM.getEntryList(tag);
                        if (entryList.isPresent()) {
                            var tagItems = entryList.get().stream().map(RegistryEntry::value).toList();
                            return entities.equals(tagItems);
                        }
                        return false;
                    });
                    //2 tags contain the same items. panic.
                    if(itemTags.count() != 1) {
                        throw new IllegalStateException("Couldn't transform entity list to a single tag");
                    }
                    inst.set("tag", itemTags.findFirst().get().id());
                }
                return inst;
            });
}
