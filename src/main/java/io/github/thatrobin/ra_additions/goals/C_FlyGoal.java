package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

public class C_FlyGoal extends Goal {

    public Predicate<Entity> condition;

    public C_FlyGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, Predicate<Entity> condition, double speed) {
        super(goalType, livingEntity);
        this.setPriority(priority);
        this.condition = condition;
        this.goal = new FlyGoal((PathAwareEntity) livingEntity, speed) {
            @Override
            public boolean canStart() {
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
        };
    }

    @Override
    public boolean doesApply(Entity entity){
        return condition == null || condition.test(entity);
    }



    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("fly"), new SerializableData()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("speed", SerializableDataTypes.DOUBLE, 1.0d)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_FlyGoal(type, entity, data.getInt("priority"), data.get("condition"), data.getDouble("speed")));
    }

}
