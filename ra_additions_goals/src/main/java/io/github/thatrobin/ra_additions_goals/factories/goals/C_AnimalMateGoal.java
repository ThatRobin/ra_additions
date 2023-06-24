package io.github.thatrobin.ra_additions_goals.factories.goals;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.Goal;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalFactory;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.passive.AnimalEntity;

public class C_AnimalMateGoal extends Goal {

    public C_AnimalMateGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, double speed) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.setGoal(new AnimalMateGoal((AnimalEntity) livingEntity, speed, AnimalEntity.class) {
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
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0)
                .add("speed", "the speed that the entity will move to its mate.", SerializableDataTypes.DOUBLE, 1.0d),
                data ->
                        (type, entity) -> new C_AnimalMateGoal(type, entity, data.getInt("priority"), data.getDouble("speed")))
                .allowCondition();
    }

}
