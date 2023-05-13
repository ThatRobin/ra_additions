package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.mob.MobEntity;

public class C_LookAroundGoal extends Goal {

    public C_LookAroundGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.setGoal(new LookAroundGoal((MobEntity) livingEntity) {
            @Override
            public boolean canStart() {
                return super.canStart() && doesApply(this.mob);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("look_around"), new SerializableDataExt()
                .add("priority", SerializableDataTypes.INT, 0),
                data ->
                        (type, entity) -> new C_LookAroundGoal(type, entity, data.getInt("priority")))
                .allowCondition();
    }

}
