package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.integration.PowerReloadCallback;
import io.github.apace100.apoli.power.PowerTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class PowerIconManager {

    private static final HashMap<Identifier, ItemStack> descriptions = new HashMap<>();

    @SuppressWarnings("deprecation")
    public PowerIconManager() {
        PowerReloadCallback.EVENT.register(this::clear);
        PowerTypes.registerAdditionalData("icon", (powerId, factoryId, isSubPower, data, powerType) -> addItem(powerId, Registries.ITEM.get(Identifier.tryParse(data.getAsString())).getDefaultStack()));
    }

    public void clear() {
        descriptions.clear();
    }

    public static ItemStack getIcon(Identifier powerId) {
        if(!descriptions.containsKey(powerId)) {
            return Items.GRASS_BLOCK.getDefaultStack();
        }
        return descriptions.get(powerId);
    }

    public void addItem(Identifier powerId, ItemStack item) {
        descriptions.put(powerId,item);
    }
}
