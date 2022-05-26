package io.github.thatrobin.ccpacksapoli.factories;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ccpacksapoli.CCPacksApoli;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class ItemConditions {

    public static void register() {
        register(new ConditionFactory<>(CCPacksApoli.identifier("compare_durability"), new SerializableData()
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.EQUAL)
                .add("compare_to", SerializableDataTypes.INT, 0),
                (data, stack) -> {
                    int durability = stack.getMaxDamage() - stack.getDamage();
                    return ((Comparison)data.get("comparison")).compare(durability, data.getInt("compare_to"));
                }));
    }

    private static void register(ConditionFactory<ItemStack> conditionFactory) {
        Registry.register(ApoliRegistries.ITEM_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }

}
