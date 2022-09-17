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
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;

import java.util.function.Predicate;

public class C_AttackWithOwnerGoal extends Goal {

    public Predicate<Entity> condition;

    public C_AttackWithOwnerGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, Predicate<Entity> condition) {
        super(goalType, livingEntity);
        this.setPriority(priority);
        this.condition = condition;
        this.goal = new AttackWithOwnerGoal((TameableEntity) livingEntity) {
            @Override
            public boolean canStart() {
                if (this.tameable.isTamed() && !this.tameable.isSitting()) {
                    LivingEntity livingEntity = this.tameable.getOwner();
                    if (livingEntity == null) {
                        return false;
                    } else {
                        this.attacking = livingEntity.getAttacking();
                        int i = livingEntity.getLastAttackTime();
                        return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.tameable.canAttackWithOwner(this.attacking, livingEntity) && doesApply(this.mob);
                    }
                } else {
                    return false;
                }
            }
        };
    }

    @Override
    public boolean doesApply(Entity entity){
        return condition == null || condition.test(entity);
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("attack_with_owner"), new SerializableData()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_AttackWithOwnerGoal(type, entity, data.getInt("priority"), data.get("condition")));
    }

}
