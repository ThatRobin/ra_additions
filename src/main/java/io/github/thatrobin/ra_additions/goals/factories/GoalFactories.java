package io.github.thatrobin.ra_additions.goals.factories;

import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.ra_additions.goals.*;
import io.github.thatrobin.ra_additions.util.RAA_Registries;
import net.minecraft.registry.Registry;

import java.util.Arrays;
import java.util.List;

public class GoalFactories {

    public static void register(String label) {
        register(C_ActiveTargetGoal.createFactory(label));
        register(C_AnimalMateGoal.createFactory(label));
        register(C_AttackGoal.createFactory(label));
        register(C_AttackWithOwnerGoal.createFactory(label));
        register(C_AvoidSunlightGoal.createFactory(label));
        register(C_BowAttackGoal.createFactory(label));
        register(C_BreakDoorGoal.createFactory(label));
        register(C_BreathAirGoal.createFactory(label));
        register(C_CatSitOnBlockGoal.createFactory(label));
        register(C_ChaseBoatGoal.createFactory(label));
        register(C_CreeperIgniteGoal.createFactory(label));
        register(C_CrossbowAttackGoal.createFactory(label));
        register(C_EscapeDangerGoal.createFactory(label));
        register(C_LookAroundGoal.createFactory(label));
        register(C_LookAtEntityGoal.createFactory(label));
        register(C_RevengeGoal.createFactory(label));
        register(C_SwimGoal.createFactory(label));
        register(C_TemptGoal.createFactory(label));
        register(C_WanderAroundGoal.createFactory(label));
    }

    private static void register(GoalFactory<?> factory, String... args) {
        List<String> argList = Arrays.stream(args).toList();
        DockyEntry entry = new DockyEntry()
                .setHeader("Types")
                .setFactory(factory);

        if(!argList.isEmpty()) entry.setDescription(argList.get(0));
        if(!(argList.size() <= 1)) entry.setExamplePath(argList.get(1));

        //DockyRegistry.register(entry);
        Registry.register(RAA_Registries.TASK_FACTORY, factory.getSerializerId(), factory);
    }

}
