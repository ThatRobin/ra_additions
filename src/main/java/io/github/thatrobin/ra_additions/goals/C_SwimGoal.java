package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import io.github.thatrobin.ra_additions.util.FluidEnum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.FluidTags;

import java.util.function.Predicate;

public class C_SwimGoal extends Goal {

    public Predicate<Entity> condition;

    public C_SwimGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, Predicate<Entity> condition, FluidEnum fluidEnum) {
        super(goalType, livingEntity);
        this.setPriority(priority);
        this.condition = condition;
        this.setTicking();
        this.goal = new SwimGoal((MobEntity) livingEntity) {
            @Override
            public boolean canStart() {
                switch(fluidEnum) {
                    case LAVA -> {
                        return this.mob.isInLava() && doesApply(mob);
                    }
                    case WATER -> {
                        return this.mob.isTouchingWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getSwimHeight() && doesApply(mob);
                    }
                    case BOTH -> {
                        return ((this.mob.isTouchingWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getSwimHeight()) || this.mob.isInLava()) && doesApply(mob);
                    }
                }
                return false;
            }
        };
    }

    @Override
    public boolean doesApply(Entity entity) {
        return condition == null || condition.test(entity);
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory(String label) {
        return new GoalFactory<>(RA_Additions.identifier("swim"), new SerializableDataExt(label)
                .add("priority", SerializableDataTypes.INT, 0)
                .add("fluid_type", SerializableDataType.enumValue(FluidEnum.class), FluidEnum.BOTH)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_SwimGoal(type, entity, data.getInt("priority"), data.get("condition"), data.get("fluid_type")));
    }

}
