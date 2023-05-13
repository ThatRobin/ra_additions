package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Pair;

public class C_ActiveTargetGoal extends Goal {


    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;

    public C_ActiveTargetGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition) {
        super(goalType, livingEntity, Type.TARGET);
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.setGoal(new ActiveTargetGoal<>((MobEntity) livingEntity, LivingEntity.class, reciprocalChance, checkVisibility, checkCanNavigate, null) {
            @Override
            public boolean canStart() {
                if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
                    return false;
                } else {
                    this.findClosestTarget();
                    return this.targetEntity != null && doesApply(this.mob);
                }
            }
            @Override
            protected void findClosestTarget() {
                this.targetEntity = this.mob.world.getClosestEntity(this.mob.world.getEntitiesByClass(LivingEntity.class, this.getSearchBox(this.getFollowRange()), livingEntity -> applyFilter(livingEntity)), this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            }
        });
    }

    public boolean applyFilter(Entity entity) {
        return (bientityCondition == null || bientityCondition.test(new Pair<>(this.entity, entity)));
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("active_target"), new SerializableDataExt()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("reciprocal_chance", SerializableDataTypes.INT, 10)
                .add("check_visibility", SerializableDataTypes.BOOLEAN, true)
                .add("check_can_navigate", SerializableDataTypes.BOOLEAN, false)
                .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_ActiveTargetGoal(type, entity, data.getInt("priority"), data.getInt("reciprocal_chance"), data.getBoolean("check_visibility"), data.getBoolean("check_can_navigate"), data.get("bientity_condition")))
                .allowCondition();
    }

}
