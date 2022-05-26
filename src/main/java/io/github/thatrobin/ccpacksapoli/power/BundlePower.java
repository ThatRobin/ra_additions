package io.github.thatrobin.ccpacksapoli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class BundlePower extends Power {

    public int hold_amount;
    public Predicate<ItemStack> item_condition;

    public BundlePower(PowerType<?> type, LivingEntity entity, int hold_amount, Predicate<ItemStack> item_condition) {
        super(type, entity);
        this.hold_amount = hold_amount;
        this.item_condition = item_condition;
    }

    public boolean doesApply(ItemStack stack) {
        return item_condition == null || item_condition.test(stack);
    }

}