package io.github.thatrobin.ccpacksapoli.power;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.thatrobin.ccpacksapoli.util.StatBarHudRender;
import net.minecraft.entity.LivingEntity;

public class StatBar extends VariableIntPower {

    private final StatBarHudRender hudRender;

    public StatBar(PowerType<?> type, LivingEntity entity, StatBarHudRender hudRender) {
        super(type, entity,20,0,20);
        this.hudRender = hudRender;
    }

    public StatBarHudRender getHudRender() {
        return this.hudRender;
    }

}
