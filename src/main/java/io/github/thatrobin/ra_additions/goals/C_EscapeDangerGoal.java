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
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;

public class C_EscapeDangerGoal extends Goal {

    public Predicate<Entity> condition;

    public C_EscapeDangerGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, Predicate<Entity> condition, double speed) {
        super(goalType, livingEntity);
        this.setPriority(priority);
        this.condition = condition;
        this.goal = new EscapeDangerGoal((PathAwareEntity) livingEntity, speed) {
            @Override
            public boolean canStart() {
                if (this.mob.getAttacker() == null && !this.mob.isOnFire()) {
                    return false;
                } else {
                    if (this.mob.isOnFire()) {
                        BlockPos blockPos = this.locateClosestWater(this.mob.world, this.mob, 5);
                        if (blockPos != null) {
                            this.targetX = blockPos.getX();
                            this.targetY = blockPos.getY();
                            this.targetZ = blockPos.getZ();
                            return doesApply(mob);
                        }
                    }

                    return this.findTarget() && doesApply(mob);
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
        return new GoalFactory<>(RA_Additions.identifier("escape_danger"), new SerializableDataExt(label)
                .add("priority", SerializableDataTypes.INT, 0)
                .add("speed", SerializableDataTypes.DOUBLE, 1.25d)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_EscapeDangerGoal(type, entity, data.getInt("priority"), data.get("condition"), data.getDouble("speed")));
    }

}
