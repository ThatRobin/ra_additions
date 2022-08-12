package io.github.thatrobin.ra_additions.mixins;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TemptGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TemptGoal.class)
public interface TemptGoalAccessorMixin {

    @Accessor("cooldown")
    int getCooldown();

    @Accessor("cooldown")
    void setCooldown(int cooldown);

    @Accessor("predicate")
    TargetPredicate getPredicate();

    @Accessor("predicate")
     void setPredicate(TargetPredicate predicate);
}
