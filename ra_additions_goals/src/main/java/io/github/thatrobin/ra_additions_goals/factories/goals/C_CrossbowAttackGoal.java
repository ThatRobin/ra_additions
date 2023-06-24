package io.github.thatrobin.ra_additions_goals.factories.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.Goal;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalFactory;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalType;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Pair;

@SuppressWarnings("unchecked")
public class C_CrossbowAttackGoal<T extends HostileEntity & RangedAttackMob & CrossbowUser> extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;

    public C_CrossbowAttackGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, double speed, float range, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.setGoal(new CrossbowAttackGoal<>((T)livingEntity, speed, range) {
            @Override
            public boolean canStart() {
                return this.hasAliveTarget() && this.isEntityHoldingCrossbow() && doesApply(this.actor.getTarget());
            }
        });
    }

    @Override
    public boolean doesApply(Entity entity) {
        return super.doesApply(this.entity) && (bientityCondition == null || bientityCondition.test(new Pair<>(this.entity, entity)));
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("crossbow_attack"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0)
                .add("speed", "The speed that it should move to its target at.", SerializableDataTypes.DOUBLE, 1.0d)
                .add("range", "The range the entity must be in to be attacked.", SerializableDataTypes.FLOAT, 15.0f)
                .add("bientity_condition", "A condition to see if it can attack its target.", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_CrossbowAttackGoal(type, entity, data.getInt("priority"), data.getDouble("speed"), data.getFloat("range"), data.get("bientity_condition")))
                .allowCondition();
    }

}
