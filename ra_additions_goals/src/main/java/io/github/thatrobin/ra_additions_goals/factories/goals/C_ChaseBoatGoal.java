package io.github.thatrobin.ra_additions_goals.factories.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.Goal;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalType;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ChaseBoatGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class C_ChaseBoatGoal extends Goal {

    private final ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition;

    public C_ChaseBoatGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, ConditionFactory<Pair<Entity, Entity>>.Instance bientityCondition) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.bientityCondition = bientityCondition;
        this.setGoal(new ChaseBoatGoal((PathAwareEntity) livingEntity) {
            @Override
            public boolean canStart() {
                List<BoatEntity> list = this.mob.getWorld().getNonSpectatingEntities(BoatEntity.class, this.mob.getBoundingBox().expand(5.0D));
                boolean bl = false;

                for (BoatEntity boatEntity : list) {
                    Entity entity = boatEntity.getControllingPassenger();
                    if (entity instanceof PlayerEntity && (MathHelper.abs(((PlayerEntity) entity).sidewaysSpeed) > 0.0F || MathHelper.abs(((PlayerEntity) entity).forwardSpeed) > 0.0F)) {
                        bl = true;
                        break;
                    }
                }

                return (this.passenger != null && (MathHelper.abs(this.passenger.sidewaysSpeed) > 0.0F || MathHelper.abs(this.passenger.forwardSpeed) > 0.0F) || bl) && doesApply(this.passenger);
            }
        });
    }

    @Override
    public boolean doesApply(Entity entity) {
        return super.doesApply(this.entity) && (bientityCondition == null || bientityCondition.test(new Pair<>(this.entity, entity)));
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("chase_boat"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0)
                .add("bientity_condition", "A condition to see if it can chase the boat.", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_ChaseBoatGoal(type, entity, data.getInt("priority"), data.get("bientity_condition")))
                .allowCondition();
    }

}
