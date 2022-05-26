package io.github.thatrobin.ccpacksapoli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class BindPower extends Power {

    private final Predicate<ItemStack> item_condition;
    private List<Integer> slots;
    private final Predicate<ItemStack> prevent_use;

    public BindPower(PowerType<?> type, LivingEntity entity, Predicate<ItemStack> item_condition, List<Integer> slots, Predicate<ItemStack> prevent_use) {
        super(type, entity);
        this.item_condition = item_condition;
        this.prevent_use = prevent_use;
        this.slots = slots;

    }

    public boolean checkSlot(int slot){
        if(slots == null) {
            return true;
        } else if(slots.contains(slot)) {
            return true;
        }
        return false;
    }

    public boolean doesPrevent(ItemStack stack) {
        return prevent_use.test(stack);
    }

    public boolean doesApply(ItemStack stack) {
        return item_condition == null || item_condition.test(stack);
    }

}

