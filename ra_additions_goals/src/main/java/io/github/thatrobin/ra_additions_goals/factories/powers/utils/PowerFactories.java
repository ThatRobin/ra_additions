package io.github.thatrobin.ra_additions_goals.factories.powers.utils;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.powers.AddGoalPower;
import io.github.thatrobin.ra_additions_goals.factories.powers.RemoveGoalPower;
import net.minecraft.registry.Registry;

import java.util.Arrays;
import java.util.List;

public class PowerFactories {

    public static void register() {
        register(AddGoalPower.createFactory(), "Adds a custom goal (from `data/{namespace}/goals/`) to the entity with this power.");
        register(RemoveGoalPower.createFactory(), "The entity with this power will have any goals listed removed from itself.");
    }

    private static void register(PowerFactory<?> factory, String description) {
        register(factory, description, RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\powers\\" + factory.getSerializerId().getPath() + "_example.json");
    }

    private static void register(PowerFactory<?> factory, String... args) {
        List<String> argList = Arrays.stream(args).toList();

        DockyEntry entry = new DockyEntry()
                .setHeader("Types")
                .setType("power_types")
                .setFactory(factory);

        if(!argList.isEmpty()) entry.setDescription(argList.get(0));
        if(RA_Additions.getExamplePathRoot() != null && !(argList.size() <= 1)) entry.setExamplePath(argList.get(1));

        DockyRegistry.register(entry);

        Registry.register(ApoliRegistries.POWER_FACTORY, factory.getSerializerId(), factory);
    }

}
