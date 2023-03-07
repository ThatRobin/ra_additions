package io.github.thatrobin.ra_additions.util;

import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;

@SuppressWarnings("rawtypes")
public class RAA_Registries {
    public static final Registry<GoalFactory> TASK_FACTORY;

    static {
        TASK_FACTORY = FabricRegistryBuilder.createSimple(GoalFactory.class, RA_Additions.identifier("task_factory")).buildAndRegister();
    }
}
