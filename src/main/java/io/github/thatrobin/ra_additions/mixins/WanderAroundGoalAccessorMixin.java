package io.github.thatrobin.ra_additions.mixins;

import net.minecraft.entity.ai.goal.WanderAroundGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WanderAroundGoal.class)
public interface WanderAroundGoalAccessorMixin {
    @Accessor("canDespawn")
    boolean getCanDespawn();
}
