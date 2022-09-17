package io.github.thatrobin.ra_additions.util;

import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.ClassDataRegistry;
import net.minecraft.entity.ai.goal.*;

public class RAA_ClassDataRegistry {

    public static void registerAll() {
        ClassDataRegistry<Goal> goalRegistry =
                ClassDataRegistry.getOrCreate(ClassUtil.castClass(Goal.class), "Goal");

        goalRegistry.addMapping("active_target", ActiveTargetGoal.class);
        goalRegistry.addMapping("animal_mate", AnimalMateGoal.class);
        goalRegistry.addMapping("attack", AttackGoal.class);
        goalRegistry.addMapping("attack_with_owner", AttackWithOwnerGoal.class);
        goalRegistry.addMapping("avoid_sunlight", AvoidSunlightGoal.class);
        goalRegistry.addMapping("bow_attack", BowAttackGoal.class);
        goalRegistry.addMapping("break_door", BreakDoorGoal.class);
        goalRegistry.addMapping("breath_air", BreatheAirGoal.class);
        goalRegistry.addMapping("cat_sit_on_block", CatSitOnBlockGoal.class);
        goalRegistry.addMapping("chase_boat", ChaseBoatGoal.class);
        goalRegistry.addMapping("creeper_ignite", CreeperIgniteGoal.class);
        goalRegistry.addMapping("crossbow_attack", CrossbowAttackGoal.class);
        goalRegistry.addMapping("disableable_follow_target", DisableableFollowTargetGoal.class);
        goalRegistry.addMapping("dive_jumping", DiveJumpingGoal.class);
        goalRegistry.addMapping("dolphin_jump", DolphinJumpGoal.class);
        goalRegistry.addMapping("door_interact", DoorInteractGoal.class);
        goalRegistry.addMapping("eat_grass", EatGrassGoal.class);
        goalRegistry.addMapping("escape_danger", EscapeDangerGoal.class);
        goalRegistry.addMapping("escape_sunlight", EscapeSunlightGoal.class);
        goalRegistry.addMapping("flee_entity", FleeEntityGoal.class);
        goalRegistry.addMapping("fly", FlyGoal.class);
        goalRegistry.addMapping("follow_group_leader", FollowGroupLeaderGoal.class);
        goalRegistry.addMapping("follow_mob", FollowMobGoal.class);
        goalRegistry.addMapping("follow_owner", FollowOwnerGoal.class);
        goalRegistry.addMapping("follow_parent", FollowParentGoal.class);
        goalRegistry.addMapping("form_caravan", FormCaravanGoal.class);
        goalRegistry.addMapping("go_to_bed_and_sleep", GoToBedAndSleepGoal.class);
        goalRegistry.addMapping("go_to_village", GoToVillageGoal.class);
        goalRegistry.addMapping("go_to_walk_target", GoToWalkTargetGoal.class);
        goalRegistry.addMapping("hold_in_hands", HoldInHandsGoal.class);
        goalRegistry.addMapping("horse_bond_with_player", HorseBondWithPlayerGoal.class);
        goalRegistry.addMapping("iron_golem_look", IronGolemLookGoal.class);
        goalRegistry.addMapping("iron_golem_wander_around", IronGolemWanderAroundGoal.class);
        goalRegistry.addMapping("long_door_interact", LongDoorInteractGoal.class);
        goalRegistry.addMapping("look_around", LookAroundGoal.class);
        goalRegistry.addMapping("look_at_customer", LookAtCustomerGoal.class);
        goalRegistry.addMapping("look_at_entity", LookAtEntityGoal.class);
        goalRegistry.addMapping("melee_attack", MeleeAttackGoal.class);
        goalRegistry.addMapping("move_into_water", MoveIntoWaterGoal.class);
        goalRegistry.addMapping("move_through_village", MoveThroughVillageGoal.class);
        goalRegistry.addMapping("move_to_raid_center", MoveToRaidCenterGoal.class);
        goalRegistry.addMapping("move_to_target_pos", MoveToTargetPosGoal.class);
        goalRegistry.addMapping("pounce_at_target", PounceAtTargetGoal.class);
        goalRegistry.addMapping("powder_snow_jump", PowderSnowJumpGoal.class);
        goalRegistry.addMapping("projectile_attack", ProjectileAttackGoal.class);
        goalRegistry.addMapping("raid", RaidGoal.class);
        goalRegistry.addMapping("revenge", RevengeGoal.class);
        goalRegistry.addMapping("sit", SitGoal.class);
        goalRegistry.addMapping("sit_on_owner_shoulder", SitOnOwnerShoulderGoal.class);
        goalRegistry.addMapping("skeleton_horse_trap_trigger", SkeletonHorseTrapTriggerGoal.class);
        goalRegistry.addMapping("step_and_destroy_block", StepAndDestroyBlockGoal.class);
        goalRegistry.addMapping("stop_and_look_at_entity", StopAndLookAtEntityGoal.class);
        goalRegistry.addMapping("stop_following_customer", StopFollowingCustomerGoal.class);
        goalRegistry.addMapping("swim_around", SwimAroundGoal.class);
        goalRegistry.addMapping("swim", SwimGoal.class);
        goalRegistry.addMapping("tempt", TemptGoal.class);
        goalRegistry.addMapping("track_iron_golem_target", TrackIronGolemTargetGoal.class);
        goalRegistry.addMapping("track_owner_attacker", TrackOwnerAttackerGoal.class);
        goalRegistry.addMapping("track_target", TrackTargetGoal.class);
        goalRegistry.addMapping("universal_anger", UniversalAngerGoal.class);
        goalRegistry.addMapping("untamed_active_target", UntamedActiveTargetGoal.class);
        goalRegistry.addMapping("wander_around_far", WanderAroundFarGoal.class);
        goalRegistry.addMapping("wander_around", WanderAroundGoal.class);
        goalRegistry.addMapping("wander_around_point_of_interest", WanderAroundPointOfInterestGoal.class);
        goalRegistry.addMapping("wander_near_target", WanderNearTargetGoal.class);
        goalRegistry.addMapping("wolf_beg", WolfBegGoal.class);
        goalRegistry.addMapping("zombie_attack", ZombieAttackGoal.class);
    }

}
