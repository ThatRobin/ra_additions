package io.github.thatrobin.ra_additions.goals.factories;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class Goal {
    protected LivingEntity entity;
    protected GoalType<?> type;

    private net.minecraft.entity.ai.goal.Goal goal;
    private int priority;
    private boolean shouldTick = false;
    private boolean shouldTickWhenInactive = false;

    protected List<Predicate<Entity>> conditions;

    public Goal(GoalType<?> type, LivingEntity entity) {
        this.type = type;
        this.entity = entity;
        this.conditions = new LinkedList<>();
    }

    @SuppressWarnings("unused")
    public Goal addCondition(Predicate<Entity> condition) {
        this.conditions.add(condition);
        return this;
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
        return true;
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

    public GoalType<?> getType() {
        return type;
    }

    public void setGoal(net.minecraft.entity.ai.goal.Goal goal) {
        this.goal = goal;
    }

    public net.minecraft.entity.ai.goal.Goal getGoal() {
        return this.goal;
    }

}
