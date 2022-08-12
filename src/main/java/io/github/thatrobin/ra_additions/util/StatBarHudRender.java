package io.github.thatrobin.ra_additions.util;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.HudRender;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class StatBarHudRender extends HudRender {

    private final String side;

    public StatBarHudRender(boolean shouldRender, int barIndex, Identifier spriteLocation, ConditionFactory<LivingEntity>.Instance condition, String side) {
        super(shouldRender, barIndex, spriteLocation, condition, false);
        this.side = side;
    }

    public String getSide() {
        return this.side;
    }

}
