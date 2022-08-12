package io.github.thatrobin.ra_additions.util;

import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.ClassDataRegistry;
import net.minecraft.entity.ai.goal.*;

public class RAA_ClassDataRegistry {

    public static void registerAll() {
        ClassDataRegistry<Goal> featureRenderer =
                ClassDataRegistry.getOrCreate(ClassUtil.castClass(Goal.class), "Goal");

        featureRenderer.addMapping("active_target", ActiveTargetGoal.class);
        featureRenderer.addMapping("animal_mate", AnimalMateGoal.class);
        featureRenderer.addMapping("attack", AttackGoal.class);
        featureRenderer.addMapping("attack_with_owner", AttackWithOwnerGoal.class);
        featureRenderer.addMapping("avoid_sunlight", AvoidSunlightGoal.class);
        featureRenderer.addMapping("bow_attack", BowAttackGoal.class);
        featureRenderer.addMapping("break_door", BreakDoorGoal.class);
        featureRenderer.addMapping("breath_air", BreatheAirGoal.class);
        featureRenderer.addMapping("cat_sit_on_block", CatSitOnBlockGoal.class);
        featureRenderer.addMapping("chase_boat", ChaseBoatGoal.class);
        featureRenderer.addMapping("creeper_ignite", CreeperIgniteGoal.class);
        featureRenderer.addMapping("crossbow_attack", CrossbowAttackGoal.class);
        featureRenderer.addMapping("disableable_follow_target", DisableableFollowTargetGoal.class);
        featureRenderer.addMapping("dive_jumping", DiveJumpingGoal.class);
        featureRenderer.addMapping("dolphin_jump", DolphinJumpGoal.class);
        featureRenderer.addMapping("door_interact", DoorInteractGoal.class);
        featureRenderer.addMapping("eat_grass", EatGrassGoal.class);
        featureRenderer.addMapping("escape_danger", EscapeDangerGoal.class);
        featureRenderer.addMapping("escape_sunlight", EscapeSunlightGoal.class);
        featureRenderer.addMapping("flee_entity", FleeEntityGoal.class);
        featureRenderer.addMapping("fly", FlyGoal.class);
        featureRenderer.addMapping("follow_group_leader", FollowGroupLeaderGoal.class);
        featureRenderer.addMapping("follow_mob", FollowMobGoal.class);
        featureRenderer.addMapping("follow_owner", FollowOwnerGoal.class);
        featureRenderer.addMapping("follow_parent", FollowParentGoal.class);
        featureRenderer.addMapping("form_caravan", FormCaravanGoal.class);
        featureRenderer.addMapping("go_to_bed_and_sleep", GoToBedAndSleepGoal.class);
        featureRenderer.addMapping("go_to_village", GoToVillageGoal.class);
        featureRenderer.addMapping("go_to_walk_target", GoToWalkTargetGoal.class);
        featureRenderer.addMapping("hold_in_hands", HoldInHandsGoal.class);
        featureRenderer.addMapping("horse_bond_with_player", HorseBondWithPlayerGoal.class);
        featureRenderer.addMapping("iron_golem_look", IronGolemLookGoal.class);
        featureRenderer.addMapping("iron_golem_wander_around", IronGolemWanderAroundGoal.class);
        featureRenderer.addMapping("long_door_interact", LongDoorInteractGoal.class);
        featureRenderer.addMapping("look_around", LookAroundGoal.class);
        featureRenderer.addMapping("look_at_customer", LookAtCustomerGoal.class);
        featureRenderer.addMapping("look_at_entity", LookAtEntityGoal.class);
        featureRenderer.addMapping("melee_attack", MeleeAttackGoal.class);
        featureRenderer.addMapping("move_into_water", MoveIntoWaterGoal.class);
        featureRenderer.addMapping("move_through_village", MoveThroughVillageGoal.class);
        featureRenderer.addMapping("move_to_raid_center", MoveToRaidCenterGoal.class);
        featureRenderer.addMapping("move_to_target_pos", MoveToTargetPosGoal.class);
        featureRenderer.addMapping("pounce_at_target", PounceAtTargetGoal.class);
        featureRenderer.addMapping("powder_snow_jump", PowderSnowJumpGoal.class);
        featureRenderer.addMapping("projectile_attack", ProjectileAttackGoal.class);
        featureRenderer.addMapping("raid", RaidGoal.class);
        featureRenderer.addMapping("revenge", RevengeGoal.class);
        featureRenderer.addMapping("sit", SitGoal.class);
        featureRenderer.addMapping("sit_on_owner_shoulder", SitOnOwnerShoulderGoal.class);
        featureRenderer.addMapping("skeleton_horse_trap_trigger", SkeletonHorseTrapTriggerGoal.class);
        featureRenderer.addMapping("step_and_destroy_block", StepAndDestroyBlockGoal.class);
        featureRenderer.addMapping("stop_and_look_at_entity", StopAndLookAtEntityGoal.class);
        featureRenderer.addMapping("stop_following_customer", StopFollowingCustomerGoal.class);
        featureRenderer.addMapping("swim_around", SwimAroundGoal.class);
        featureRenderer.addMapping("swim", SwimGoal.class);
        featureRenderer.addMapping("tempt", TemptGoal.class);
        featureRenderer.addMapping("track_iron_golem_target", TrackIronGolemTargetGoal.class);
        featureRenderer.addMapping("track_owner_attacker", TrackOwnerAttackerGoal.class);
        featureRenderer.addMapping("track_target", TrackTargetGoal.class);
        featureRenderer.addMapping("universal_anger", UniversalAngerGoal.class);
        featureRenderer.addMapping("untamed_active_target", UntamedActiveTargetGoal.class);
        featureRenderer.addMapping("wander_around_far", WanderAroundFarGoal.class);
        featureRenderer.addMapping("wander_around", WanderAroundGoal.class);
        featureRenderer.addMapping("wander_around_point_of_interest", WanderAroundPointOfInterestGoal.class);
        featureRenderer.addMapping("wander_near_target", WanderNearTargetGoal.class);
        featureRenderer.addMapping("wolf_beg", WolfBegGoal.class);
        featureRenderer.addMapping("zombie_attack", ZombieAttackGoal.class);
    }

}
