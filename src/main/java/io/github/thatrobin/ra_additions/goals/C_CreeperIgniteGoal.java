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
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.mob.CreeperEntity;

import java.util.function.Predicate;

public class C_CreeperIgniteGoal extends Goal {

    public Predicate<Entity> condition;

    public C_CreeperIgniteGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, Predicate<Entity> condition) {
        super(goalType, livingEntity);
        this.setPriority(priority);
        this.condition = condition;
        this.goal = new CreeperIgniteGoal((CreeperEntity) livingEntity) {
            @Override
            public boolean canStart() {
                LivingEntity livingEntity = this.creeper.getTarget();
                return this.creeper.getFuseSpeed() > 0 || livingEntity != null && this.creeper.squaredDistanceTo(livingEntity) < 9.0D && doesApply(this.creeper);
            }
        };
    }

    @Override
    public boolean doesApply(Entity entity){
        return condition == null || condition.test(entity);
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory(String label) {
        return new GoalFactory<>(RA_Additions.identifier("creeper_ignite"), new SerializableDataExt(label)
                .add("priority", SerializableDataTypes.INT, 0)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_CreeperIgniteGoal(type, entity, data.getInt("priority"), data.get("condition")));
    }

}
