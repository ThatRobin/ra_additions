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
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Pair;

public class C_CreeperIgniteGoal extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;

    public C_CreeperIgniteGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.setGoal(new CreeperIgniteGoal((CreeperEntity) livingEntity) {
            @Override
            public boolean canStart() {
                LivingEntity livingEntity = this.creeper.getTarget();
                return this.creeper.getFuseSpeed() > 0 || livingEntity != null && this.creeper.squaredDistanceTo(livingEntity) < 9.0D && doesApply(livingEntity);
            }
        });
    }

    @Override
    public boolean doesApply(Entity entity) {
        return super.doesApply(this.entity) && (bientityCondition == null || bientityCondition.test(new Pair<>(this.entity, entity)));
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("creeper_ignite"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0)
                .add("bientity_condition", "A condition to see if the target should cause the creeper to ignite", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_CreeperIgniteGoal(type, entity, data.getInt("priority"), data.get("bientity_condition")))
                .allowCondition();
    }

}
