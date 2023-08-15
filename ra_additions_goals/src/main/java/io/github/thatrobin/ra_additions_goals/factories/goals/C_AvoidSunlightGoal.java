package io.github.thatrobin.ra_additions_goals.factories.goals;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.Goal;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalFactory;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.AvoidSunlightGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class C_AvoidSunlightGoal extends Goal {

    public C_AvoidSunlightGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.setGoal(new AvoidSunlightGoal((PathAwareEntity) livingEntity) {
            @Override
            public boolean canStart() {
                return this.mob.getWorld().isDay() && NavigationConditions.hasMobNavigation(this.mob) && doesApply(this.mob);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("avoid_sunlight"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0),
                data ->
                        (type, entity) -> new C_AvoidSunlightGoal(type, entity, data.getInt("priority")))
                .allowCondition();
    }

}
