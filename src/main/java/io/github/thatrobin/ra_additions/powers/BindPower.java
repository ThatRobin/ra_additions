package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class BindPower extends Power {

    private final Predicate<ItemStack> item_condition;
    private final List<Integer> slots;
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
        } else return slots.contains(slot);
    }

    public boolean doesPrevent(ItemStack stack) {
        return prevent_use.test(stack);
    }

    public boolean doesApply(ItemStack stack) {
        return item_condition == null || item_condition.test(stack);
    }

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("bind_item"),
                new SerializableDataExt()
                        .add("item_condition", "If specified, only make the items that fulfill the specified item condition are unable to leave the players inventory.", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("prevent_use_condition", "If specified, it won't let the player use the items that fulfill the specified item condition.", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("slots", "If specified, only make the items that are in the listed inventory slots are unable to leave the players inventory. See the \"Item Stack Slot\" column of [Positioned Item Stack Slots](https://origins.readthedocs.io/en/latest/misc/extras/positioned_item_stack_slots/) for possible values.", SerializableDataTypes.INTS, null),
                data ->
                        (type, entity) -> new BindPower(type, entity, data.get("item_condition"), data.get("slots"), data.get("prevent_use_condition")))
                .allowCondition();
    }

}

