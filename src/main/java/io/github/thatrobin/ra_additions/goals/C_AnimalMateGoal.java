package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.passive.AnimalEntity;

public class C_AnimalMateGoal extends Goal {

    public C_AnimalMateGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, double chance) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.setGoal(new AnimalMateGoal((AnimalEntity) livingEntity, chance, AnimalEntity.class) {
            @Override
            public boolean canStart() {
                if (!this.animal.isInLove()) {
                    return false;
                } else {
                    this.mate = this.findMate();
                    return this.mate != null && doesApply(this.animal);
                }
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("animal_mate"), new SerializableDataExt()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("chance", SerializableDataTypes.DOUBLE, 1.0d),
                data ->
                        (type, entity) -> new C_AnimalMateGoal(type, entity, data.getInt("priority"), data.getDouble("chance")))
                .allowCondition();
    }

}
