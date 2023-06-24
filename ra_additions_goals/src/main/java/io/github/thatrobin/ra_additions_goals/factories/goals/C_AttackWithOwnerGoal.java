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
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.Pair;

public class C_AttackWithOwnerGoal extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance attackingCondition;
    private final ConditionFactory<Pair<Entity, Entity>>.Instance ownerCondition;

    public C_AttackWithOwnerGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, ConditionFactory<Pair<Entity, Entity>>.Instance attackingCondition, ConditionFactory<Pair<Entity, Entity>>.Instance ownerCondition) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.attackingCondition = attackingCondition;
        this.ownerCondition = ownerCondition;
        this.setGoal(new AttackWithOwnerGoal((TameableEntity) livingEntity) {
            @Override
            public boolean canStart() {
                if (this.tameable.isTamed() && !this.tameable.isSitting()) {
                    LivingEntity livingEntity = this.tameable.getOwner();
                    if (livingEntity == null) {
                        return false;
                    } else {
                        this.attacking = livingEntity.getAttacking();
                        int i = livingEntity.getLastAttackTime();
                        return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.tameable.canAttackWithOwner(this.attacking, livingEntity) && doesApply(this.attacking, livingEntity);
                    }
                } else {
                    return false;
                }
            }
        });
    }

    public boolean doesApply(Entity attacking, Entity owner) {
        return super.doesApply(this.entity) && doesApplyAttacking(attacking) && doesApplyOwner(owner);
    }

    public boolean doesApplyOwner(Entity entity) {
        return (ownerCondition == null || ownerCondition.test(new Pair<>(this.entity, entity)));
    }

    public boolean doesApplyAttacking(Entity entity) {
        return (attackingCondition == null || attackingCondition.test(new Pair<>(this.entity, entity)));
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("attack_with_owner"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0)
                .add("attacking_condition", "A condition to see if it can attack its target.", ApoliDataTypes.BIENTITY_CONDITION, null)
                .add("owner_condition", "A condition to see if it can attack with the owner.", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_AttackWithOwnerGoal(type, entity, data.getInt("priority"), data.get("attacking_condition"), data.get("owner_condition")))
                .allowCondition();
    }

}
