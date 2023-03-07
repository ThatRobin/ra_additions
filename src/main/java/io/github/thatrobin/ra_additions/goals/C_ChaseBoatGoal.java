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
import net.minecraft.entity.ai.goal.ChaseBoatGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Predicate;

public class C_ChaseBoatGoal extends Goal {

    public Predicate<Entity> condition;

    public C_ChaseBoatGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, Predicate<Entity> condition) {
        super(goalType, livingEntity);
        this.setPriority(priority);
        this.condition = condition;
        this.goal = new ChaseBoatGoal((PathAwareEntity) livingEntity) {
            @Override
            public boolean canStart() {
                List<BoatEntity> list = this.mob.world.getNonSpectatingEntities(BoatEntity.class, this.mob.getBoundingBox().expand(5.0D));
                boolean bl = false;

                for (BoatEntity boatEntity : list) {
                    Entity entity = boatEntity.getPrimaryPassenger();
                    if (entity instanceof PlayerEntity && (MathHelper.abs(((PlayerEntity) entity).sidewaysSpeed) > 0.0F || MathHelper.abs(((PlayerEntity) entity).forwardSpeed) > 0.0F)) {
                        bl = true;
                        break;
                    }
                }

                return (this.passenger != null && (MathHelper.abs(this.passenger.sidewaysSpeed) > 0.0F || MathHelper.abs(this.passenger.forwardSpeed) > 0.0F) || bl) && doesApply(this.mob);
            }
        };
    }

    @Override
    public boolean doesApply(Entity entity){
        return condition == null || condition.test(entity);
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory(String label) {
        return new GoalFactory<>(RA_Additions.identifier("chase_boat"), new SerializableDataExt(label)
                .add("priority", SerializableDataTypes.INT, 0)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_ChaseBoatGoal(type, entity, data.getInt("priority"), data.get("condition")));
    }

}
