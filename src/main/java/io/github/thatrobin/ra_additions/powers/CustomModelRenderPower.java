package io.github.thatrobin.ra_additions.powers;

import com.google.common.collect.Lists;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.client.TestArmorItem;
import io.github.thatrobin.ra_additions.client.TestArmorRenderer;
import io.github.thatrobin.ra_additions.data.RAA_DataTypes;
import io.github.thatrobin.ra_additions.mixins.GeoArmorRendererAccessor;
import io.github.thatrobin.ra_additions.registry.ItemRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

public class CustomModelRenderPower<T extends Item & GeoItem> extends Power {

    private static final List<Pair<EquipmentSlot, TestArmorItem>> SLOTS = Lists.newArrayList(new Pair<>(EquipmentSlot.HEAD,ItemRegistry.POWER_ARMOR_HELMET),
                new Pair<>(EquipmentSlot.CHEST,ItemRegistry.POWER_ARMOR_CHESTPLATE),
                new Pair<>(EquipmentSlot.LEGS,ItemRegistry.POWER_ARMOR_LEGGINGS),
                new Pair<>(EquipmentSlot.FEET,ItemRegistry.POWER_ARMOR_BOOTS));

    private final Identifier path;
    private final List<Pair<EquipmentSlot, TestArmorItem>> items;

    public CustomModelRenderPower(PowerType<?> type, LivingEntity entity, Identifier path, List<Pair<EquipmentSlot, TestArmorItem>> items) {
        super(type, entity);
        this.path = path;
        this.items = items;
    }

    public List<Pair<EquipmentSlot, TestArmorItem>> getItems() {
        return items;
    }


    public Identifier getPath() {
        return path;
    }

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("custom_model_render"),
                new SerializableDataExt()
                        .add("model_path", SerializableDataTypes.IDENTIFIER, new Identifier(GeckoLib.MOD_ID, "armor/gecko_armor"))
                        .add("slots", RAA_DataTypes.DISPLAY_MODEL_TYPES, SLOTS),
                data ->
                        (type, entity) -> new CustomModelRenderPower<>(type, entity, data.getId("model_path"), data.get("slots")))
                .allowCondition();
    }

}
