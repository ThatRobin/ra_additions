package io.github.thatrobin.ra_additions_goals.factories.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.ClassDataRegistry;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Pair;

import java.util.*;

public class RemoveGoalPower extends Power {

    private final List<String> classStrings = new LinkedList<>();
    private final List<Pair<Goal,Integer>> cachedGoals = new ArrayList<>();

    public RemoveGoalPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public void addClass(String cls) {
        classStrings.add(cls);
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public boolean doesApply(Class<? extends Goal> cls) {
        Optional<ClassDataRegistry> optionalCdr = ClassDataRegistry.get(ClassUtil.castClass(Goal.class));
        if(optionalCdr.isPresent()) {
            ClassDataRegistry<? extends Goal> cdr = optionalCdr.get();
            return classStrings.stream()
                    .map(cdr::mapStringToClass)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .anyMatch(c -> c.isAssignableFrom(cls));
        }
        return false;
    }

    public void onAdded() {
        if(entity instanceof MobEntity mobEntity) {
            mobEntity.goalSelector.getGoals().forEach((goal) -> {
                if(doesApply(goal.getGoal().getClass())) {
                    boolean removeGoal = true;
                    for (AddGoalPower power : PowerHolderComponent.getPowers(entity, AddGoalPower.class)) {
                        if (power.getGoals().stream().anyMatch((goal1 -> goal1.getGoal().equals(goal.getGoal()))))
                            removeGoal = false;
                    }
                    if (removeGoal) {
                        Pair<Goal, Integer> goalIntegerPair = new Pair<>(goal.getGoal(), goal.getPriority());
                        cachedGoals.add(goalIntegerPair);
                    }
                }
            });
            for (Pair<Goal, Integer> cachedGoal : cachedGoals) {
                mobEntity.goalSelector.remove(cachedGoal.getLeft());
            }
        }
    }

    public void onRemoved() {
        if(entity instanceof MobEntity mobEntity) {
            cachedGoals.forEach((goalIntegerPair -> mobEntity.goalSelector.add(goalIntegerPair.getRight(), goalIntegerPair.getLeft())));
        }
    }

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("remove_goal"),
                new SerializableDataExt()
                        .add("goal", "The goal to remove from the mob.", SerializableDataTypes.STRING, null)
                        .add("goals", "The goals to remove from the mob.", SerializableDataTypes.STRINGS, null),
                data ->
                        (type, entity) -> {
                            RemoveGoalPower power = new RemoveGoalPower(type, entity);
                            data.ifPresent("goal", power::addClass);
                            data.<List<String>>ifPresent("goals",
                                    list -> list.forEach(power::addClass));
                            return power;
                        })
                .allowCondition();
    }
}
