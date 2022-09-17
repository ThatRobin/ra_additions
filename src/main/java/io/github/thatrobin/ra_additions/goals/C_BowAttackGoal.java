package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.AvoidSunlightGoal;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;

import java.util.function.Predicate;

public class C_BowAttackGoal extends Goal {

    public Predicate<Entity> condition;

    public C_BowAttackGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, Predicate<Entity> condition, double speed, int attackInterval, float range) {
        super(goalType, livingEntity);
        this.setPriority(priority);
        this.condition = condition;
        this.goal = new BowAttackGoal((HostileEntity) livingEntity, speed, attackInterval, range) {
            @Override
            public boolean canStart() {
                return this.actor.getTarget() == null ? false : this.isHoldingBow() && doesApply(this.actor);
            }
        };
    }

    @Override
    public boolean doesApply(Entity entity){
        return condition == null || condition.test(entity);
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("bow_attack"), new SerializableData()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("speed", SerializableDataTypes.DOUBLE, 1.0d)
                .add("attack_interval", SerializableDataTypes.INT, 20)
                .add("range", SerializableDataTypes.FLOAT, 15.0f)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_BowAttackGoal(type, entity, data.getInt("priority"), data.get("condition"), data.getDouble("speed"), data.getInt("attack_interval"), data.getFloat("range")));
    }

}
