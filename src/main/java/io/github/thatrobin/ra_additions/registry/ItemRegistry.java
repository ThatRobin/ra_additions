package io.github.thatrobin.ra_additions.registry;

import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.client.TestArmorItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemRegistry {

    public static final TestArmorItem POWER_ARMOR_HELMET = new TestArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Settings());
    public static final TestArmorItem POWER_ARMOR_CHESTPLATE = new TestArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Settings());
    public static final TestArmorItem POWER_ARMOR_LEGGINGS = new TestArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, new Item.Settings());
    public static final TestArmorItem POWER_ARMOR_BOOTS = new TestArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, new Item.Settings());

    public static void register() {
        register("power_armor_helmet", POWER_ARMOR_HELMET);
        register("power_armor_chestplate", POWER_ARMOR_CHESTPLATE);
        register("power_armor_leggings", POWER_ARMOR_LEGGINGS);
        register("power_armor_boots", POWER_ARMOR_BOOTS);
    }

    public static <I extends Item> I register(String name, I item) {
        return Registry.register(Registries.ITEM, new Identifier(RA_Additions.MODID, name), item);
    }

}
