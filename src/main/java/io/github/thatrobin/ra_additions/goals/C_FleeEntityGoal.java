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
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Pair;

@SuppressWarnings({"rawtypes", "unchecked"})
public class C_FleeEntityGoal extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;

    public C_FleeEntityGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, float fleeDistance, double fleeSlowSpeed, double fleeFastSpeed, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.setGoal(new FleeEntityGoal((PathAwareEntity) livingEntity, LivingEntity.class, fleeDistance, fleeSlowSpeed, fleeFastSpeed, (object) -> true) {
            @Override
            public boolean canStart() {
                return super.canStart() && doesApply(this.targetEntity);
            }
        });
    }

    @Override
    public boolean doesApply(Entity entity) {
        return super.doesApply(this.entity) && (bientityCondition == null || bientityCondition.test(new Pair<>(this.entity, entity)));
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("flee_entity"), new SerializableDataExt()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("distance", SerializableDataTypes.FLOAT, 8.0f)
                .add("slow_speed", SerializableDataTypes.DOUBLE, 1.6)
                .add("fast_speed", SerializableDataTypes.DOUBLE, 1.4)
                .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_FleeEntityGoal(type, entity, data.getInt("priority"), data.getFloat("distance"), data.getDouble("slow_speed"), data.getDouble("fast_speed"), data.get("bientity_condition")))
                .allowCondition();
    }

}
