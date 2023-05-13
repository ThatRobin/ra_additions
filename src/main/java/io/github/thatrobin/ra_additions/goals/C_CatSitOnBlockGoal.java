package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
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
                .add("priority", SerializableDataTypes.INT, 0)
                .add("speed", SerializableDataTypes.DOUBLE, 1.0d),
                data ->
                        (type, entity) -> new C_CatSitOnBlockGoal(type, entity, data.getInt("priority"), data.getDouble("speed")))
                .allowCondition();
    }

}
