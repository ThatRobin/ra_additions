package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;

public class ItemConditionsServer {

    public static void register() {

    }

    @SuppressWarnings({"SameParameterValue", "unused"})
    private static void register(ConditionFactory<ItemStack> factory, String description) {
        DockyEntry entry = new DockyEntry()
                .setHeader("Condition Types")
                .setFactory(factory)
                .setDescription(description)
                .setType("item_condition_types");
        if(RA_Additions.getExamplePathRoot() != null) entry.setExamplePath(RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\conditions\\item\\" + factory.getSerializerId().getPath() + "_example.json");
        DockyRegistry.register(entry);
        Registry.register(ApoliRegistries.ITEM_CONDITION, factory.getSerializerId(), factory);
    }

}
