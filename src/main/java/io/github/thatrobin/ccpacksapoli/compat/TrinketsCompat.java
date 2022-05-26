package io.github.thatrobin.ccpacksapoli.compat;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.StackPowerUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TrinketsCompat {
    public static boolean trinketCheck(PlayerEntity player, ConditionFactory<ItemStack>.Instance condition) {
        AtomicBoolean found = new AtomicBoolean(false);
        TrinketComponent component = TrinketsApi.getTrinketComponent(player).get();
        component.forEach((slotReference, itemStack) -> {
            if (condition.test(itemStack)) {
                found.set(true);
            }
        });
        return found.get();
    }

}
