package io.github.thatrobin.ra_additions_goals.factories.powers;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.Goal;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class AddGoalPower extends Power {

    private final List<Goal> goals = Lists.newArrayList();
    private boolean canPathThroughDoors;

    public AddGoalPower(PowerType<?> type, LivingEntity entity, Identifier taskID, List<Identifier> taskIDs) {
        super(type, entity);
        if (taskID != null) {
            this.goals.add(GoalRegistry.get(taskID).create(entity));
        }
        if (taskIDs != null) {
            taskIDs.forEach((identifier -> this.goals.add(GoalRegistry.get(identifier).create(entity))));
        }
    }

    public void onAdded() {
        if(entity instanceof MobEntity mobEntity) {
            this.canPathThroughDoors = ((MobNavigation)mobEntity.getNavigation()).method_35140();
            ((MobNavigation)mobEntity.getNavigation()).setCanPathThroughDoors(true);
            this.goals.forEach((task) -> {
                if(task.getType().equals(Goal.Type.GOAL)) {
                    mobEntity.goalSelector.add(3, task.getGoal());
                }
                if(task.getType().equals(Goal.Type.TARGET)) {
                    mobEntity.targetSelector.add(3, task.getGoal());
                }
            });
        }
    }

    public void onRemoved() {
        if(entity instanceof MobEntity mobEntity) {
            ((MobNavigation)mobEntity.getNavigation()).setCanPathThroughDoors(this.canPathThroughDoors);
            this.goals.forEach((task) -> {
                if (mobEntity.goalSelector.getGoals().stream().anyMatch((prioritizedGoal -> prioritizedGoal.getGoal() == task.getGoal()))) {
                    mobEntity.goalSelector.remove(task.getGoal());
                }
                if (mobEntity.targetSelector.getGoals().stream().anyMatch((prioritizedGoal -> prioritizedGoal.getGoal() == task.getGoal()))) {
                    mobEntity.targetSelector.remove(task.getGoal());
                }
            });
        }
    }

    public List<Goal> getGoals() {
        return goals;
    }

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("add_goal"),
                new SerializableDataExt()
                        .add("goal", "The goal to add to the mob.", SerializableDataTypes.IDENTIFIER, null)
                        .add("goals", "The goals to add to the mob.", SerializableDataTypes.IDENTIFIERS, null),
                data ->
                        (type, entity) -> new AddGoalPower(type, entity, data.getId("goal"), data.get("goals")))
                .allowCondition();
    }
}

