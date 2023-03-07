package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.docky.utils.SectionTitleManager;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class ItemConditions {

    public static void register(String label) {
        SectionTitleManager.put("Condition Types", "item_condition");

        register(new ConditionFactory<>(RA_Additions.identifier("evaluate_condition"), new SerializableDataExt(label)
                .add("item_condition", "The Identifier of the tag or condition file to be evaluated", SerializableDataTypes.STRING),
                (data, stack) -> {
                    String idStr = data.getString("item_condition");
                    if(idStr.startsWith("#")) {
                        Identifier id = Identifier.tryParse(idStr.substring(1));
                        Collection<ConditionType> conditions = ItemConditionTagManager.CONDITION_TAG_LOADER.getTag(id);
                        boolean result = true;
                        for (ConditionType condition : conditions) {
                            if(!condition.getCondition().test(stack)) {
                                result = false;
                            }
                        }
                        return result;
                    } else {
                        Identifier id = Identifier.tryParse(idStr);
                        ConditionFactory<ItemStack>.Instance condition =  ItemConditionRegistry.get(id).getCondition();
                        return condition.test(stack);
                    }
                }));
    }

    private static void register(ConditionFactory<ItemStack> factory) {
         Registry.register(ApoliRegistries.ITEM_CONDITION, factory.getSerializerId(), factory);
    }
}
