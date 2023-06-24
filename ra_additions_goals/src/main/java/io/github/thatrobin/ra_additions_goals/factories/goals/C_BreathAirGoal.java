package io.github.thatrobin.ra_additions_goals.factories.goals;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.Goal;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalFactory;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BreatheAirGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class C_BreathAirGoal extends Goal {

    public C_BreathAirGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.setGoal(new BreatheAirGoal((PathAwareEntity) livingEntity) {
            @Override
            public boolean canStart() {
                return this.mob.getAir() < 140 && doesApply(this.mob);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("breath_air"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0),
                data ->
                        (type, entity) -> new C_BreathAirGoal(type, entity, data.getInt("priority")))
                .allowCondition();
    }

}
