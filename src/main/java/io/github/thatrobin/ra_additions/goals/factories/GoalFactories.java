package io.github.thatrobin.ra_additions.goals.factories;

import io.github.thatrobin.ra_additions.goals.*;
import io.github.thatrobin.ra_additions.util.GoalFactorySupplier;
import io.github.thatrobin.ra_additions.util.RAA_Registries;
import net.minecraft.util.registry.Registry;

public class GoalFactories {

    public static void register() {
        register(C_EscapeDangerGoal::createFactory);
        register(C_LookAroundGoal::createFactory);
        register(C_LookAtEntityGoal::createFactory);
        register(C_SwimGoal::createFactory);
        register(C_TemptGoal::createFactory);
        register(C_WanderAroundGoal::createFactory);
    }

    private static void register(GoalFactory<?> serializer) {
        Registry.register(RAA_Registries.TASK_FACTORY, serializer.getSerializerId(), serializer);
    }

    private static void register(GoalFactorySupplier<?> factorySupplier) {
        register(factorySupplier.createFactory());
    }

}
