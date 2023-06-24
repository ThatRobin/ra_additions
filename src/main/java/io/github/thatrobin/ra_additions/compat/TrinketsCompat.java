package io.github.thatrobin.ra_additions.compat;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

public class TrinketsCompat {
    public static boolean trinketCheck(PlayerEntity player, ConditionFactory<ItemStack>.Instance condition) {
        boolean found = false;
        if(TrinketsApi.getTrinketComponent(player).isPresent()) {
            TrinketComponent component = TrinketsApi.getTrinketComponent(player).get();
            for (Pair<SlotReference, ItemStack> slotReferenceItemStackPair : component.getAllEquipped()) {
                ItemStack stack = slotReferenceItemStackPair.getRight();
                if (condition.test(stack)) {
                    found = true;
                }
            }
        }
        return found;
    }
}
