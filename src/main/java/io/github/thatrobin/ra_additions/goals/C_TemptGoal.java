package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.mixins.TemptGoalAccessorMixin;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Pair;

public class C_TemptGoal extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;
    private final LivingEntity livingEntity;

    public C_TemptGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition, double speed, boolean canBeScared) {
        super(goalType, livingEntity);
        this.livingEntity = livingEntity;
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.setGoal(new DDTemptGoal((PathAwareEntity) livingEntity, speed, null, canBeScared));
    }

    @Override
    public boolean doesApply(Entity entity) {
        return bientityCondition == null || bientityCondition.test(new Pair<>(livingEntity, entity));
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("tempt"), new SerializableData()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("speed", SerializableDataTypes.DOUBLE, 1.2d)
                .add("can_be_scared", SerializableDataTypes.BOOLEAN, false)
                .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_TemptGoal(type, entity, data.getInt("priority"), data.get("bientity_condition"), data.getDouble("speed"), data.getBoolean("can_be_scared")));
    }

    class DDTemptGoal extends TemptGoal {

        public DDTemptGoal(PathAwareEntity pathAwareEntity, double speed, Ingredient food, boolean canBeScared) {
            super(pathAwareEntity, speed, food, canBeScared);
        }

        @Override
        public boolean canStart() {
            if (((TemptGoalAccessorMixin)this).getCooldown() > 0) {
                ((TemptGoalAccessorMixin)this).setCooldown(((TemptGoalAccessorMixin)this).getCooldown()-1);
                return false;
            } else {
                this.closestPlayer = this.mob.world.getClosestPlayer(TargetPredicate.createNonAttackable().setBaseMaxDistance(10.0D).ignoreVisibility(), this.mob);
                return this.closestPlayer != null && doesApply(this.closestPlayer);
            }
        }
    }

}
