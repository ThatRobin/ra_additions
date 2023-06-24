package io.github.thatrobin.ra_additions_goals.factories.goals.utils;

import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.goals.*;
import io.github.thatrobin.ra_additions_goals.registries.RAA_Registries;
import net.minecraft.registry.Registry;

import java.util.Arrays;
import java.util.List;

public class GoalFactories {

    public static void register() {
        register(C_ActiveTargetGoal.createFactory(), "A target goal that finds a target by entity class when the goal starts.");
        register(C_AnimalMateGoal.createFactory(), "A goal that causes its mob to find a mate to breed with.");
        register(C_AttackGoal.createFactory(), "A goal that causes its mob to follow and attack its selected target.");
        register(C_AttackWithOwnerGoal.createFactory(), "A goal that causes its mob to target whoever its owner attacks.");
        register(C_AvoidSunlightGoal.createFactory(), "A goal that causes its mob to avoid sunlight.");
        register(C_BowAttackGoal.createFactory(), "A goal that allows its mob to attack using its bow, if one if found.");
        register(C_BreakDoorGoal.createFactory(), "A goal that allows its mob to break down doors.");
        register(C_BreathAirGoal.createFactory(), "A goal that allows its mob to search for air to breath in.");
        register(C_CatSitOnBlockGoal.createFactory(), "A goal that allows a cat to sit on a block.");
        register(C_ChaseBoatGoal.createFactory(), "A goal that causes its mob to chase boats.");
        register(C_CreeperIgniteGoal.createFactory(), "A goal that allows creepers to ignite");
        register(C_CrossbowAttackGoal.createFactory(), "A goal that allows Crossbow using mobs to attack with their crossbow.");
        register(C_DoorInteractGoal.createFactory(), "A goal that allows the mob to interact with doors.");
        register(C_EatGrassGoal.createFactory(), "A goal that causes the mob to eat grass");
        register(C_EscapeDangerGoal.createFactory(), "A goal that causes the mob to run away when hit.");
        register(C_FleeEntityGoal.createFactory(), "A goal that causes the mob to run away from an entity.");
        register(C_FollowMobGoal.createFactory(), "A goal that causes the mob to follow an entity.");
        register(C_LookAroundGoal.createFactory(), "A goal that causes the mob to look around.");
        register(C_LookAtEntityGoal.createFactory(), "A goal that causes the mob to look at the closest entity.");
        register(C_MeleeAttackGoal.createFactory(), "A goal that causes the mob to use melee attacks against the target.");
        register(C_RevengeGoal.createFactory(), "A goal that causes the entity to seek revenge on the entity that hits it.");
        register(C_SwimGoal.createFactory(), "A goal that causes the entity to swim in water.");
        register(C_TemptGoal.createFactory(), "A goal that causes the entity to be tempted.");
        register(C_WanderAroundGoal.createFactory(), "A goal that causes the entity to wander around.");
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
