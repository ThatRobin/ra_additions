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
import net.minecraft.entity.ai.goal.BreatheAirGoal;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CatEntity;

import java.util.function.Predicate;

public class C_CatSitOnBlockGoal extends Goal {

    public Predicate<Entity> condition;

    public C_CatSitOnBlockGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, Predicate<Entity> condition, double speed) {
        super(goalType, livingEntity);
        this.setPriority(priority);
        this.condition = condition;
        this.goal = new CatSitOnBlockGoal((CatEntity) livingEntity, speed) {
            @Override
            public boolean canStart() {
                return this.cat.isTamed() && !this.cat.isSitting() && super.canStart() && doesApply(this.mob);
            }
        };
    }

    @Override
    public boolean doesApply(Entity entity){
        return condition == null || condition.test(entity);
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("cat_sit_on_block"), new SerializableData()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("speed", SerializableDataTypes.DOUBLE, 1.0d)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_CatSitOnBlockGoal(type, entity, data.getInt("priority"), data.get("condition"), data.getDouble("speed")));
    }

}
