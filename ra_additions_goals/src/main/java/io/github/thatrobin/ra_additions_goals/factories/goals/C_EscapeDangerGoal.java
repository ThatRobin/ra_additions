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
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public class C_EscapeDangerGoal extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;

    public C_EscapeDangerGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, double speed, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.setGoal(new EscapeDangerGoal((PathAwareEntity) livingEntity, speed) {
            @Override
            public boolean canStart() {
                if (this.mob.getAttacker() == null && !this.mob.isOnFire()) {
                    return false;
                } else {
                    if (this.mob.isOnFire()) {
                        BlockPos blockPos = this.locateClosestWater(this.mob.getWorld(), this.mob, 5);
                        if (blockPos != null) {
                            this.targetX = blockPos.getX();
                            this.targetY = blockPos.getY();
                            this.targetZ = blockPos.getZ();
                            return doesApply(this.mob.getAttacker());
                        }
                    }

                    return this.findTarget() && doesApply(this.mob.getAttacker());
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
        return new GoalFactory<>(RA_Additions.identifier("escape_danger"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0)
                .add("speed", "The speed it should escape at", SerializableDataTypes.DOUBLE, 1.25d)
                .add("bientity_condition", "A condition to check if it should run away from the target.", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_EscapeDangerGoal(type, entity, data.getInt("priority"), data.getDouble("speed"), data.get("bientity_condition")))
                .allowCondition();
    }

}
