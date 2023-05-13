package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class C_WanderAroundGoal extends Goal {

    public C_WanderAroundGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, double speed) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.setGoal(new WanderAroundGoal((PathAwareEntity) livingEntity, speed) {
            @Override
            public boolean canStart() {
                if (this.mob.hasPassengers()) {
                    return false;
                } else {
                    if (!this.ignoringChance) {
                        if (this.canDespawn && this.mob.getDespawnCounter() >= 100) {
                            return false;
                        }

                        if (this.mob.getRandom().nextInt(toGoalTicks(this.chance)) != 0) {
                            return false;
                        }
                    }

                    Vec3d vec3d = this.getWanderTarget();
                    if (vec3d == null) {
                        return false;
                    } else {
                        this.targetX = vec3d.x;
                        this.targetY = vec3d.y;
                        this.targetZ = vec3d.z;
                        this.ignoringChance = false;
                        return doesApply(this.mob);
                    }
                }
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("wander_around"), new SerializableDataExt()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("speed", SerializableDataTypes.DOUBLE, 1.0d),
                data ->
                        (type, entity) -> new C_WanderAroundGoal(type, entity, data.getInt("priority"), data.getDouble("speed")))
                .allowCondition();
    }

}
