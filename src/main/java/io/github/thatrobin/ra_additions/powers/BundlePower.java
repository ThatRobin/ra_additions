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

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory(String label) {
        return new PowerFactory<>(RA_Additions.identifier("use_as_bundle"),
                new SerializableDataExt(label)
                        .add("max_amount", "the amount of items you can store in the bundle.", SerializableDataTypes.INT, 64)
                        .add("item_condition", "Items that fulfil this condition, will be bundle-like.", ApoliDataTypes.ITEM_CONDITION),
                data ->
                        (type, entity) -> new BundlePower(type, entity, data.getInt("max_amount"), data.get("item_condition")))
                .allowCondition();
    }
}