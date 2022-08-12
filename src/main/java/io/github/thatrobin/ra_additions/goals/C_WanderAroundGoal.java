package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.mixins.WanderAroundGoalAccessorMixin;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

public class C_WanderAroundGoal extends Goal {

    public Predicate<Entity> condition;

    public C_WanderAroundGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, Predicate<Entity> condition, double speed) {
        super(goalType, livingEntity);
        this.setPriority(priority);
        this.condition = condition;
        this.setGoal(new DDWanderAroundGoal((PathAwareEntity) livingEntity, speed));
    }

    @Override
    public boolean doesApply(Entity entity){
        return condition == null || condition.test(entity);
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("wander_around"), new SerializableData()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("speed", SerializableDataTypes.DOUBLE, 1.0d)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_WanderAroundGoal(type, entity, data.getInt("priority"), data.get("condition"), data.getDouble("speed")));
    }

    class DDWanderAroundGoal extends WanderAroundGoal {

        public DDWanderAroundGoal(PathAwareEntity pathAwareEntity, double speed) {
            super(pathAwareEntity, speed);
        }

        @Override
        public boolean canStart() {
            if (this.mob.hasPassengers()) {
                return false;
            } else {
                if (!this.ignoringChance) {
                    if (((WanderAroundGoalAccessorMixin)this).getCanDespawn() && this.mob.getDespawnCounter() >= 100) {
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
    }

}
