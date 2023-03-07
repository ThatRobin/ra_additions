package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.ra_additions.powers.*;
import net.minecraft.registry.Registry;

import java.util.Arrays;
import java.util.List;

public class PowerFactories {

    public static void register(String label) {
        register(ActionOnProjectileLand.createFactory(label), "Executes an Entity Action and a Block Action at the location that a thrown projectile lands.");
        register(AddGoalPower.createFactory(label), "Adds a custom goal (from `data/{namespace}/goals/`) to the entity with this power.");
        register(BindPower.createFactory(label), "Makes certain items unable to leave in the entity's inventory until death.");
        register(BorderPower.createFactory(label), "Creates a border around the entity with this power. Only entities that fulfil the conditions may pass through it.");
        register(BossBarPower.createFactory(label), "Defines a boss bar for the player. Essentially identical to a [Resource Bar](https://origins.readthedocs.io/en/latest/types/power_types/resource/) but displays as a boss bar.");
        register(BundlePower.createFactory(label), "Allows an item to be used like a bundle, with a customizable capacity.");
        register(CustomTogglePower.createFactory(label), "A custom version of Apoli's [Toggle (Power Type)](https://origins.readthedocs.io/en/latest/types/power_types/toggle/)");
        register(ItemUsePower.createFactory(label), "This power uses the existing item interaction system in Minecraft to execute actions. which means that when used, the actions won't happen if something of higher priority occurs (for example opening a chest).");
        register(RemoveGoalPower.createFactory(label), "The entity with this power will have any goals listed removed from itself.");
        register(StatBarPower.createFactory(label), "Defines a stat bar for the player. Holds a persistent integer value between 0, and 20.");
        register(ValuePower.createFactory(label), "Defines a value for the player. Essentially identical to a [Resource Bar](https://origins.readthedocs.io/en/latest/types/power_types/resource/) but displays as number in a string.");
    }

    private static void register(PowerFactory<?> factory, String description) {
        register(factory, description, "C:\\Users\\robin\\IdeaProjects\\ra_additions\\run\\saves\\New World\\datapacks\\Test Pack\\data\\test_pack\\powers\\" + factory.getSerializerId().getPath() + "_example.json");
    }

    private static void register(PowerFactory<?> factory, String... args) {
        List<String> argList = Arrays.stream(args).toList();

        DockyEntry entry = new DockyEntry()
                .setHeader("Types")
                .setFactory(factory);

        if(!argList.isEmpty()) entry.setDescription(argList.get(0));
        if(!(argList.size() <= 1)) entry.setExamplePath(argList.get(1));

        DockyRegistry.register(entry);

        Registry.register(ApoliRegistries.POWER_FACTORY, factory.getSerializerId(), factory);
    }

}
