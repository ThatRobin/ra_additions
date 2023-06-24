package io.github.thatrobin.ra_additions_goals.factories.goals;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.Goal;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalFactory;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.passive.CatEntity;

public class C_CatSitOnBlockGoal extends Goal {

    public C_CatSitOnBlockGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, double speed) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.setGoal(new CatSitOnBlockGoal((CatEntity) livingEntity, speed) {
            @Override
            public boolean canStart() {
                return this.cat.isTamed() && !this.cat.isSitting() && super.canStart() && doesApply(this.mob);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("cat_sit_on_block"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0)
                .add("speed", "The speed that the cat will move to the block at.", SerializableDataTypes.DOUBLE, 1.0d),
                data ->
                        (type, entity) -> new C_CatSitOnBlockGoal(type, entity, data.getInt("priority"), data.getDouble("speed")))
                .allowCondition();
    }

}
