package io.github.thatrobin.ra_additions_goals.factories.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalFactory;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.Goal;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Pair;

public class C_TemptGoal extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;

    public C_TemptGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition, double speed, boolean canBeScared) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.setGoal(new TemptGoal((PathAwareEntity) livingEntity, speed, null, canBeScared) {
            @Override
            public boolean canStart() {
                if (this.cooldown > 0) {
                    this.cooldown -= 1;
                    return false;
                } else {
                    this.closestPlayer = this.mob.getWorld().getClosestPlayer(TargetPredicate.createNonAttackable().setBaseMaxDistance(10.0D).ignoreVisibility(), this.mob);
                    return this.closestPlayer != null && doesApply(this.closestPlayer);
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
        return new GoalFactory<>(RA_Additions.identifier("tempt"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0)
                .add("speed", "The speed it should follow at", SerializableDataTypes.DOUBLE, 1.2d)
                .add("can_be_scared", "Can the entity be scared off?", SerializableDataTypes.BOOLEAN, true)
                .add("bientity_condition", "A condition to check if it should follow the target.", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_TemptGoal(type, entity, data.getInt("priority"), data.get("bientity_condition"), data.getDouble("speed"), data.getBoolean("can_be_scared")))
                .allowCondition();
    }

}
