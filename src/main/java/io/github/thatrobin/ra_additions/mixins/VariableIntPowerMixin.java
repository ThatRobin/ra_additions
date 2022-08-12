package io.github.thatrobin.ra_additions.mixins;

import io.github.apace100.apoli.power.VariableIntPower;
import io.github.thatrobin.ra_additions.util.VariableIntPowerAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VariableIntPower.class)
public class VariableIntPowerMixin implements VariableIntPowerAccessor {

    @Mutable
    @Shadow @Final protected int max;

    @Override
    public void setMax(int max) {
        this.max = max;
    }
}
