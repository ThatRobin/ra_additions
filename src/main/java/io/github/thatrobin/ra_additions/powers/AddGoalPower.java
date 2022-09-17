package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class AddGoalPower extends Power {

    private Goal goal = null;
    private final List<Goal> goals = Lists.newArrayList();

    public AddGoalPower(PowerType<?> type, LivingEntity entity, Identifier taskID, List<Identifier> taskIDs) {
        super(type, entity);
        if(taskID != null) {
            this.goal = GoalRegistry.get(taskID).create(entity);
        }
        taskIDs.forEach((identifier -> this.goals.add(GoalRegistry.get(taskID).create(entity))));
        if(entity instanceof MobEntity mobEntity) {
            if(mobEntity.goalSelector.getGoals().stream().anyMatch((prioritizedGoal -> prioritizedGoal.getGoal() instanceof FlyGoal))) {
                this.setTicking();
            }
        }
    }

    public void onAdded() {
        if(entity instanceof MobEntity mobEntity) {
            if(this.goal != null) {
                mobEntity.goalSelector.add(goal.getPriority(), goal.getGoal());
            }
            this.goals.forEach((task) -> mobEntity.goalSelector.add(task.getPriority(), task.getGoal()));
        }
    }

    public void onRemoved() {
        if(entity instanceof MobEntity mobEntity) {
            if(this.goal != null && mobEntity.goalSelector.getGoals().stream().anyMatch((prioritizedGoal -> prioritizedGoal.getGoal() == this.goal.getGoal()))) {
                mobEntity.goalSelector.remove(goal.getGoal());
            }
            this.goals.forEach((task) -> {
                if (mobEntity.goalSelector.getGoals().stream().anyMatch((prioritizedGoal -> prioritizedGoal.getGoal() == task.getGoal()))) {
                    mobEntity.goalSelector.remove(task.getGoal());
                }
            });
        }
    }

    @Override
    public void tick() {
        if(entity instanceof MobEntity mobEntity) {
            if(mobEntity.goalSelector.getGoals().stream().anyMatch((prioritizedGoal -> prioritizedGoal.getGoal() instanceof FlyGoal))) {
                this.entity.setVelocity(this.entity.getVelocity().multiply(1.0D, 0.6D, 1.0D));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("add_goal"),
                new SerializableData()
                        .add("goal", SerializableDataTypes.IDENTIFIER, null)
                        .add("goals", SerializableDataTypes.IDENTIFIERS, Lists.newArrayList()),
                data ->
                        (type, entity) -> new AddGoalPower(type, entity, data.getId("goal"), data.get("goals")))
                .allowCondition();
    }
}

