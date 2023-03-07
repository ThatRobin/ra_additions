package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;

import java.util.function.Predicate;

public class C_ActiveTargetGoal extends Goal {

    public Predicate<Entity> condition;

    public C_ActiveTargetGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, Predicate<Entity> condition, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate) {
        super(goalType, livingEntity);
        this.setPriority(priority);
        this.condition = condition;
        this.goal = new ActiveTargetGoal<>((MobEntity) livingEntity, LivingEntity.class, reciprocalChance, checkVisibility, checkCanNavigate, null) {
            @Override
            public boolean canStart() {
                if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
                    return false;
                } else {
                    this.findClosestTarget();
                    return this.targetEntity != null && doesApply(this.mob);
                }
            }
        };
    }

    @Override
    public boolean doesApply(Entity entity){
        return condition == null || condition.test(entity);
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory(String label) {
        return new GoalFactory<>(RA_Additions.identifier("active_target"), new SerializableDataExt(label)
                .add("priority", SerializableDataTypes.INT, 0)
                .add("reciprocal_chance", SerializableDataTypes.INT, 10)
                .add("check_visibility", SerializableDataTypes.BOOLEAN)
                .add("check_can_navigate", SerializableDataTypes.BOOLEAN, false)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_ActiveTargetGoal(type, entity, data.getInt("priority"), data.get("condition"), data.getInt("reciprocal_chance"), data.getBoolean("check_visibility"), data.getBoolean("check_can_navigate")));
    }

}
