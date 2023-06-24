package io.github.thatrobin.ra_additions.data;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.DataTypeRedirector;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.docky.utils.SerializableDataTypeExt;
import io.github.thatrobin.docky.utils.SerializableDataTypesRegistry;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.client.TestArmorItem;
import io.github.thatrobin.ra_additions.registry.ItemRegistry;
import io.github.thatrobin.ra_additions.util.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;

import static io.github.thatrobin.ra_additions.RA_Additions.getExamplePathRoot;

public class RAA_DataTypes {

    static {
        SerializableDataTypesRegistry.register(RAA_DataTypes.class);
        DataTypeRedirector.register("bientity_action", "../bientity_action_types.md");
        DataTypeRedirector.register("bientity_condition", "../bientity_condition_types.md");
        DataTypeRedirector.register("block_action", "../block_action_types.md");
        DataTypeRedirector.register("block_condition", "../block_condition_types.md");
        DataTypeRedirector.register("entity_action", "../entity_action_types.md");
        DataTypeRedirector.register("entity_condition", "../entity_condition_types.md");
        DataTypeRedirector.register("item_action", "../item_action_types.md");
        DataTypeRedirector.register("item_condition", "../item_condition_types.md");
        DataTypeRedirector.register("backwards_compatible_key", "https://origins.readthedocs.io/en/latest/types/data_types/key/");
    }

    public static final SerializableDataType<RecipeType<?>> RECIPE_TYPE = SerializableDataTypeExt.registry(ClassUtil.castClass(RecipeType.class), Registries.RECIPE_TYPE);

    public static final SerializableDataType<StatBarHudRender> STAT_BAR_HUD_RENDER = SerializableDataTypeExt.compound(
            "stat_bar_hud_render",
            "An [Object](object.md) used to define how a stat bar should be rendered.",
            StatBarHudRender.class,
            new SerializableDataExt()
                    .add("should_render", "Whether the bar should be visible or not.", SerializableDataTypes.BOOLEAN, true)
                    .add("bar_index", "The indexed position of the bar on the sprite to use. Please note that indexes start at 0.", SerializableDataTypes.INT, 0)
                    .add("side", "Determines which side of the players HUD the bar appears on. Can be `\"left\"` or `\"right\"`.", SerializableDataTypes.STRING, "right")
                    .add("sprite_location", "The path to the file in the assets which contains what the bar looks like. The base mod doesn't include any bars, but you can create your own using Resource Packs.", SerializableDataTypes.IDENTIFIER, RA_Additions.identifier("textures/gui/icons.png"))
                    .add("condition", "If set (and `should_render` is true), the bar will only display when the entity with the power fulfills this condition.", ApoliDataTypes.ENTITY_CONDITION, null),
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
            }, getExamplePathRoot() + "/testdata/ra_additions/docky_data_type_examples/stat_bar_hud_render.json");

    public static final SerializableDataType<BossBarHudRenderOverlay> BOSS_BAR_HUD_RENDER_OVERLAY = SerializableDataTypeExt.compound(
            "boss_bar_hud_render_overlay",
            "An [Object](object.md) used to define how a boss bar overlay should be rendered.",
            BossBarHudRenderOverlay.class,
            new SerializableDataExt()
                    .add("should_render", "Whether the bar should be visible or not.", SerializableDataTypes.BOOLEAN, true)
                    .add("bar_index", "The indexed position of the bar on the sprite to use. Please note that indexes start at 0.", SerializableDataTypes.INT, 0)
                    .add("priority", "The order in which the bar appears on the screen.", SerializableDataTypes.INT, 0)
                    .add("sprite_location", "The path to the file in the assets which contains what the bar looks like.", SerializableDataTypes.IDENTIFIER, new Identifier("textures/gui/bars.png"))
                    .add("condition", "If set (and `should_render` is true), the bar will only display when the entity with the power fulfills this condition.", ApoliDataTypes.ENTITY_CONDITION, null)
                    .add("inverted", "Determines whether greater values increment or decrement the bar.", SerializableDataTypes.BOOLEAN, false),
            (dataInst) -> new BossBarHudRenderOverlay(
                    dataInst.getBoolean("should_render"),
                    dataInst.getInt("bar_index"),
                    dataInst.getInt("priority"),
                    dataInst.getId("sprite_location"),
                    dataInst.get("condition"),
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
            }, getExamplePathRoot() + "/testdata/ra_additions/docky_data_type_examples/boss_bar_hud_overlay.json");

    public static final SerializableDataType<List<BossBarHudRenderOverlay>> BOSS_BAR_HUD_RENDER_OVERLAYS = SerializableDataTypeExt.list(RAA_DataTypes.BOSS_BAR_HUD_RENDER_OVERLAY);

    public static final SerializableDataType<BossBarHudRender> BOSS_BAR_HUD_RENDER = SerializableDataTypeExt.compound(
            "boss_bar_hud_render",
            "An [Object](object.md) used to define how a boss bar should be rendered.",
            BossBarHudRender.class,
            new SerializableDataExt()
                    .add("should_render", "Whether the bar should be visible or not.", SerializableDataTypes.BOOLEAN, true)
                    .add("bar_index", "The indexed position of the bar on the sprite to use. Please note that indexes start at 0.", SerializableDataTypes.INT, 0)
                    .add("priority", "The order in which the bar appears on the screen.", SerializableDataTypes.INT, 0)
                    .add("overlays", "The overlays you can display on the boss bar.", RAA_DataTypes.BOSS_BAR_HUD_RENDER_OVERLAYS, null)
                    .add("sprite_location", "The path to the file in the assets which contains what the bar looks like.", SerializableDataTypes.IDENTIFIER, new Identifier("textures/gui/bars.png"))
                    .add("condition", "If set (and `should_render` is true), the bar will only display when the entity with the power fulfills this condition.", ApoliDataTypes.ENTITY_CONDITION, null)
                    .add("inverted", "Determines whether greater values increment or decrement the bar.", SerializableDataTypes.BOOLEAN, false),
            (dataInst) -> new BossBarHudRender(
                    dataInst.getBoolean("should_render"),
                    dataInst.getInt("bar_index"),
                    dataInst.getInt("priority"),
                    dataInst.get("overlays"),
                    dataInst.getId("sprite_location"),
                    dataInst.get("condition"),
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
            }, getExamplePathRoot() + "/testdata/ra_additions/docky_data_type_examples/boss_bar_hud_render.json");

    public static final SerializableDataType<KeybindingData> KEYBINDING = SerializableDataType.compound(KeybindingData.class,
            new SerializableDataExt()
                    .add("key", "A string that defines what key this binding will use.\n", SerializableDataTypes.STRING)
                    .add("category", "The category this key will fall under.", SerializableDataTypes.STRING),
            dataInstance -> new KeybindingData(dataInstance.get("key"), dataInstance.get("key"), dataInstance.get("category")),
            (serializableData, keyBinding) -> {
                SerializableData.Instance data = serializableData.new Instance();
                data.set("key", keyBinding.keyKey());
                data.set("category", keyBinding.category());
                return data;
            });

    public static final SerializableDataType<Pair<EquipmentSlot, TestArmorItem>> DISPLAY_MODEL_TYPE =
            SerializableDataTypeExt.mapped(
                    "display_model_type",
                    "A [String](string.md) used to define which piece of armour a display model should be rendered on",
                    ClassUtil.castClass(Pair.class), HashBiMap.create(ImmutableMap.of(
                    "head", new Pair<>(EquipmentSlot.HEAD,ItemRegistry.POWER_ARMOR_HELMET),
                    "chest", new Pair<>(EquipmentSlot.CHEST,ItemRegistry.POWER_ARMOR_CHESTPLATE),
                    "legs", new Pair<>(EquipmentSlot.LEGS,ItemRegistry.POWER_ARMOR_LEGGINGS),
                    "feet", new Pair<>(EquipmentSlot.FEET,ItemRegistry.POWER_ARMOR_BOOTS)
            )));

    public static final SerializableDataType<List<Pair<EquipmentSlot, TestArmorItem>>> DISPLAY_MODEL_TYPES = SerializableDataTypeExt.list(RAA_DataTypes.DISPLAY_MODEL_TYPE);

    public static final SerializableDataType<ItemStack> ITEM_OR_ITEM_STACK = new SerializableDataType<>(ItemStack.class,
            SerializableDataTypes.ITEM_STACK::send, SerializableDataTypes.ITEM_STACK::receive, jsonElement -> {
        if(jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
            Item item = SerializableDataTypes.ITEM.read(jsonElement);
            return new ItemStack(item);
        }
        return SerializableDataTypes.ITEM_STACK.read(jsonElement);
    });
}
