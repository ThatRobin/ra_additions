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
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Pair;

@SuppressWarnings("unchecked")
public class C_BowAttackGoal<T extends HostileEntity & RangedAttackMob> extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;

    public C_BowAttackGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, double speed, int attackInterval, float range, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.setGoal(new BowAttackGoal<>((T) livingEntity, speed, attackInterval, range) {
            @Override
            public boolean canStart() {
                return this.actor.getTarget() != null && this.isHoldingBow() && doesApply(this.actor.getTarget());
            }
        });
    }

    @Override
    public boolean doesApply(Entity entity) {
        return super.doesApply(this.entity) && (bientityCondition == null || bientityCondition.test(new Pair<>(this.entity, entity)));
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("bow_attack"), new SerializableDataExt()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("speed", SerializableDataTypes.DOUBLE, 1.0d)
                .add("attack_interval", SerializableDataTypes.INT, 20)
                .add("range", SerializableDataTypes.FLOAT, 15.0f)
                .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_BowAttackGoal(type, entity, data.getInt("priority"), data.getDouble("speed"), data.getInt("attack_interval"), data.getFloat("range"), data.get("bientity_condition")))
                .allowCondition();
    }

}
