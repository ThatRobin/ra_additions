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
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Pair;

public class C_LookAtEntityGoal extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;
    private final LivingEntity livingEntity;

    public C_LookAtEntityGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition, float range) {
        super(goalType, livingEntity);
        this.livingEntity = livingEntity;
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.goal = new LookAtEntityGoal((MobEntity) livingEntity, LivingEntity.class, range) {
            @Override
            public boolean canStart() {
                if (this.mob.getRandom().nextFloat() >= this.chance) {
                    return false;
                } else {
                    if (this.mob.getTarget() != null) {
                        this.target = this.mob.getTarget();
                    }

                    this.target = this.mob.world.getClosestEntity(this.mob.world.getEntitiesByClass(this.targetType, this.mob.getBoundingBox().expand(this.range, 3.0D, this.range), C_LookAtEntityGoal.this::doesApply), targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());

                    return this.target != null;
                }
            }
        };
    }

    @Override
    public boolean doesApply(Entity entity) {
        return bientityCondition == null || bientityCondition.test(new Pair<>(livingEntity, entity));
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory(String label) {
        return new GoalFactory<>(RA_Additions.identifier("look_at_entity"), new SerializableDataExt(label)
                .add("priority", SerializableDataTypes.INT, 0)
                .add("range", SerializableDataTypes.FLOAT, 6.0f)
                .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_LookAtEntityGoal(type, entity, data.getInt("priority"), data.get("bientity_condition"), data.getFloat("range")));
    }

}
