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
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.passive.AnimalEntity;

import java.util.function.Predicate;

public class C_AnimalMateGoal extends Goal {

    public Predicate<Entity> condition;

    public C_AnimalMateGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, Predicate<Entity> condition, double chance) {
        super(goalType, livingEntity);
        this.setPriority(priority);
        this.condition = condition;
        this.goal = new AnimalMateGoal((AnimalEntity) livingEntity, chance, AnimalEntity.class) {
            @Override
            public boolean canStart() {
                if (!this.animal.isInLove()) {
                    return false;
                } else {
                    this.mate = this.findMate();
                    return this.mate != null && doesApply(this.animal);
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
        return new GoalFactory<>(RA_Additions.identifier("animal_mate"), new SerializableDataExt(label)
                .add("priority", SerializableDataTypes.INT, 0)
                .add("chance", SerializableDataTypes.DOUBLE, 1.0d)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_AnimalMateGoal(type, entity, data.getInt("priority"), data.get("condition"), data.getDouble("chance")));
    }

}
