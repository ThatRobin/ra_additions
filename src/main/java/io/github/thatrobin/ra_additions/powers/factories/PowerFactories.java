package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.powers.*;
import net.minecraft.registry.Registry;

import java.util.Arrays;
import java.util.List;

public class PowerFactories {

    public static void register() {
        register(ActionOnProjectileLand.createFactory(), "Executes an Entity Action and a Block Action at the location that a thrown projectile lands.");
        register(AnimatedOverlayPower.createFactory(), "Lets you have a texture overlay onto the entity that you can have change over time.");
        register(BindPower.createFactory(), "Makes certain items unable to leave in the entity's inventory until death.");
        register(BorderPower.createFactory(), "Creates a border around the entity with this power. Only entities that fulfil the conditions may pass through it.");
        register(BossBarPower.createFactory(), "Defines a boss bar for the player. Essentially identical to a [Resource Bar](https://origins.readthedocs.io/en/latest/types/power_types/resource/) but displays as a boss bar.");
        register(BundlePower.createFactory(), "Allows an item to be used like a bundle, with a customizable capacity.");
        register(CustomModelRenderPower.createFactory(), "Allows a GeckoLib model to be rendered on the player.");
        register(CustomTogglePower.createFactory(), "A custom version of Apoli's [Toggle (Power Type)](https://origins.readthedocs.io/en/latest/types/power_types/toggle/)");
        register(ItemUsePower.createFactory(), "This power uses the existing item interaction system in Minecraft to execute actions. which means that when used, the actions won't happen if something of higher priority occurs (for example opening a chest).");
        register(StatBarPower.createFactory(), "Defines a stat bar for the player. Holds a persistent integer value between 0, and 20.");
        register(ValuePower.createFactory(), "Defines a value for the player. Essentially identical to a [Resource Bar](https://origins.readthedocs.io/en/latest/types/power_types/resource/) but displays as number in a string.");
        register(FurnacePower.createFactory(), "Creates an instance of a furnace in the player that can be accessed using the [Use Internal Block]() Entity Action");
        register(BrewingStandPower.createFactory(), "Creates an instance of a brewing stand in the player that can be accessed using the [Use Internal Block]() Entity Action");
        register(NbtPower.createFactory());
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
