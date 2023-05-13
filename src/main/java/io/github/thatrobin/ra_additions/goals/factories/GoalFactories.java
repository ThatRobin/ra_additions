package io.github.thatrobin.ra_additions.goals.factories;

import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.*;
import io.github.thatrobin.ra_additions.util.RAA_Registries;
import net.minecraft.registry.Registry;

import java.util.Arrays;
import java.util.List;

public class GoalFactories {

    public static void register() {
        register(C_ActiveTargetGoal.createFactory(), "1");
        register(C_AnimalMateGoal.createFactory(), "");
        register(C_AttackGoal.createFactory(), "");
        register(C_AttackWithOwnerGoal.createFactory(), "");
        register(C_AvoidSunlightGoal.createFactory(), "");
        register(C_BowAttackGoal.createFactory(), "");
        register(C_BreakDoorGoal.createFactory(), "");
        register(C_BreathAirGoal.createFactory(), "");
        register(C_CatSitOnBlockGoal.createFactory(), "");
        register(C_ChaseBoatGoal.createFactory(), "");
        register(C_CreeperIgniteGoal.createFactory(), "");
        register(C_CrossbowAttackGoal.createFactory(), "");
        register(C_DoorInteractGoal.createFactory(), "");
        register(C_EatGrassGoal.createFactory(), "");
        register(C_EscapeDangerGoal.createFactory(), "");
        register(C_FleeEntityGoal.createFactory(), "");
        register(C_FollowMobGoal.createFactory(), "");
        register(C_LookAroundGoal.createFactory(), "");
        register(C_LookAtEntityGoal.createFactory(), "");
        register(C_MeleeAttackGoal.createFactory(), "");
        register(C_RevengeGoal.createFactory(), "");
        register(C_SwimGoal.createFactory(), "");
        register(C_TemptGoal.createFactory(), "");
        register(C_WanderAroundGoal.createFactory(), "");
    }

    private static void register(GoalFactory<?> factory, String description) {
        register(factory, description, RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\goals\\" + factory.getSerializerId().getPath() + "_example.json");
    }

    private static void register(GoalFactory<?> factory, String... args) {
        List<String> argList = Arrays.stream(args).toList();
        DockyEntry entry = new DockyEntry()
                .setHeader("Types")
                .setType("task_types")
                .setFactory(factory);

        if(!argList.isEmpty()) entry.setDescription(argList.get(0));
        if(RA_Additions.getExamplePathRoot() != null &&!(argList.size() <= 1)) entry.setExamplePath(argList.get(1));

        DockyRegistry.register(entry);
        Registry.register(RAA_Registries.TASK_FACTORY, factory.getSerializerId(), factory);
    }

}
