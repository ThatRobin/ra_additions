package io.github.thatrobin.ra_additions_goals.registries;

import io.github.apace100.calio.ClassUtil;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.Goal;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalFactory;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;

@SuppressWarnings("deprecation")
public class RAA_Registries {
    public static final Registry<GoalFactory<? extends Goal>> TASK_FACTORY;

    static {
        TASK_FACTORY = FabricRegistryBuilder.createSimple(ClassUtil.<GoalFactory<? extends Goal>>castClass(GoalFactory.class), RA_Additions.identifier("task_factory")).buildAndRegister();
    }
}
