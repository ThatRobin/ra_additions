package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;

public class RevengePower extends Power {

    private Goal revengeGoal;
    private Goal attackGoal;
    private Goal escapeDangerGoal = null;
    private int escapeDangerGoalPriority;

    public RevengePower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
        if(entity instanceof PathAwareEntity pathAwareEntity) {
            this.revengeGoal = new RevengeGoal(pathAwareEntity);
            this.attackGoal = new MeleeAttackGoal(pathAwareEntity, 1.0d, true);
        }
    }

    public void onAdded() {
        if(entity instanceof MobEntity mobEntity && revengeGoal != null) {
            mobEntity.goalSelector.getGoals().forEach((goal) -> {
                if(goal.getGoal() instanceof EscapeDangerGoal) {
                    escapeDangerGoal = goal.getGoal();
                    escapeDangerGoalPriority = goal.getPriority();
                }
            });
            mobEntity.goalSelector.remove(escapeDangerGoal);
            mobEntity.goalSelector.add(3, attackGoal);
            mobEntity.targetSelector.add(3, revengeGoal);
        }
    }

    public void onRemoved() {
        if(entity instanceof MobEntity mobEntity && revengeGoal != null) {
            if(mobEntity.goalSelector != null) {
                mobEntity.goalSelector.add(escapeDangerGoalPriority, escapeDangerGoal);
            }
            assert mobEntity.goalSelector != null;
            mobEntity.goalSelector.remove(attackGoal);
            mobEntity.targetSelector.remove(revengeGoal);
        }
    }
}
