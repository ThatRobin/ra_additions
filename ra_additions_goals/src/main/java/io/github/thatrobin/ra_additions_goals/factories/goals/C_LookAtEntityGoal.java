package io.github.thatrobin.ra_additions_goals.factories.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.Goal;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalFactory;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Pair;

public class C_LookAtEntityGoal extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;

    public C_LookAtEntityGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, float range, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.setGoal(new LookAtEntityGoal((MobEntity) livingEntity, LivingEntity.class, range) {
            @Override
            public boolean canStart() {
                if (this.mob.getRandom().nextFloat() >= this.chance) {
                    return false;
                } else {
                    if (this.mob.getTarget() != null) {
                        this.target = this.mob.getTarget();
                    }

                    this.target = this.mob.getWorld().getClosestEntity(this.mob.getWorld().getEntitiesByClass(this.targetType, this.mob.getBoundingBox().expand(this.range, 3.0D, this.range), C_LookAtEntityGoal.this::doesApply), targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
                    return this.target != null;
                }
            }
        });
    }

    @Override
    public boolean doesApply(Entity entity) {
        return super.doesApply(this.entity) && (bientityCondition == null || bientityCondition.test(new Pair<>(this.entity, entity)));
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("look_at_entity"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0)
                .add("range", "The range in which the target can be looked at.", SerializableDataTypes.FLOAT, 6.0f)
                .add("bientity_condition", "A condition to check if it should look at the target.", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_LookAtEntityGoal(type, entity, data.getInt("priority"), data.getFloat("range"), data.get("bientity_condition")))
                .allowCondition();
    }

}
