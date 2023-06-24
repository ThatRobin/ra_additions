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
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Pair;

public class C_RevengeGoal extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;

    public C_RevengeGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition) {
        super(goalType, livingEntity, Type.TARGET);
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.setGoal(new RevengeGoal((PathAwareEntity) livingEntity) {
            @Override
            public boolean canStart() {
                return super.canStart() && doesApply(this.mob.getAttacker());
            }
        });
    }

    @Override
    public boolean doesApply(Entity entity) {
        return super.doesApply(this.entity) && (bientityCondition == null || bientityCondition.test(new Pair<>(this.entity, entity)));
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("revenge"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0)
                .add("bientity_condition", "A condition to check if it should attack the target.", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_RevengeGoal(type, entity, data.getInt("priority"), data.get("bientity_condition")))
                .allowCondition();
    }

}
