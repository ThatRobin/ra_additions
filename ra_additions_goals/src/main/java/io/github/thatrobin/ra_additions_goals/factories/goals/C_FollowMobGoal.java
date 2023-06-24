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
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Pair;

public class C_FollowMobGoal extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;

    public C_FollowMobGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, double speed, float minDistance, float maxDistance, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.setGoal(new FollowMobGoal((MobEntity) livingEntity, speed, minDistance, maxDistance) {
            @Override
            public boolean canStart() {
                return super.canStart() && doesApply(this.mob.getTarget());
            }
        });
    }

    @Override
    public boolean doesApply(Entity entity) {
        return super.doesApply(this.entity) && (bientityCondition == null || bientityCondition.test(new Pair<>(this.entity, entity)));
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("follow_mob"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0)
                .add("speed", "The speed it should follow at", SerializableDataTypes.DOUBLE, 1.2d)
                .add("can_be_scared", "Can the entity be scared?", SerializableDataTypes.BOOLEAN, false)
                .add("bientity_condition", "A condition to check if it should follow the target.", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_FollowMobGoal(type, entity, data.getInt("priority"), data.getDouble("speed"), data.getFloat("min_distance"), data.getFloat("max_distance"), data.get("bientity_condition")))
                .allowCondition();
    }

}
