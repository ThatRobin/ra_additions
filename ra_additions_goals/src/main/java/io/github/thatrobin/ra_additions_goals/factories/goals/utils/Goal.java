package io.github.thatrobin.ra_additions_goals.factories.goals.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class Goal {
    protected LivingEntity entity;
    protected GoalType<?> goalType;

    private net.minecraft.entity.ai.goal.Goal goal;
    private final Type type;
    private int priority;
    private boolean shouldTick = false;
    private boolean shouldTickWhenInactive = false;

    protected List<Predicate<Entity>> conditions;

    public Goal(GoalType<?> goalType, LivingEntity entity, Type type) {
        this.goalType = goalType;
        this.entity = entity;
        this.type = type;
        this.conditions = new LinkedList<>();
    }

    public void addCondition(Predicate<Entity> condition) {
        this.conditions.add(condition);
    }

    protected void setTicking() {
        this.setTicking(false);
    }

    @SuppressWarnings("all")
    protected void setTicking(boolean evenWhenInactive) {
        this.shouldTick = true;
        this.shouldTickWhenInactive = evenWhenInactive;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean doesApply(Entity entity){
        return conditions.stream().allMatch(entityPredicate -> entityPredicate.test(entity));
    }

    @SuppressWarnings("unused")
    public boolean shouldTick() {
        return shouldTick;
    }

    @SuppressWarnings("unused")
    public boolean shouldTickWhenInactive() {
        return shouldTickWhenInactive;
    }

    @SuppressWarnings("unused")
    public void tick() {
    }

    @SuppressWarnings("unused")
    public NbtElement toTag() {
        return new NbtCompound();
    }

    @SuppressWarnings("unused")
    public void fromTag(NbtElement tag) {

    }

    public GoalType<?> getGoalType() {
        return goalType;
    }

    public void setGoal(net.minecraft.entity.ai.goal.Goal goal) {
        this.goal = goal;
    }

    public net.minecraft.entity.ai.goal.Goal getGoal() {
        return this.goal;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        TARGET,
        GOAL
    }

}
