package io.github.thatrobin.ra_additions.util;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Objects;

public class BossBarHudRender {

    private final boolean shouldRender;
    private final int barIndex;
    private final int priority;
    private final List<BossBarHudRenderOverlay> overlays;
    private final Identifier spriteLocation;
    private final ConditionFactory<LivingEntity>.Instance playerCondition;
    private final boolean inverted;

    public BossBarHudRender(boolean shouldRender, int barIndex, int priority, List<BossBarHudRenderOverlay> overlays, Identifier spriteLocation, ConditionFactory<LivingEntity>.Instance condition, boolean inverted) {
        this.shouldRender = shouldRender;
        this.barIndex = barIndex;
        this.priority = priority;
        this.overlays = overlays;
        this.spriteLocation = spriteLocation;
        this.playerCondition = condition;
        this.inverted = inverted;
    }

    public Identifier getSpriteLocation() {
        return spriteLocation;
    }

    public int getBarIndex() {
        return barIndex;
    }

    public int getPriority() {
        return priority;
    }

    public List<BossBarHudRenderOverlay> getOverlays() {
        return Objects.requireNonNullElseGet(overlays, Lists::newArrayList);
    }

    public boolean isInverted() {
        return inverted;
    }

    public boolean shouldRender() {
        return shouldRender;
    }

    public boolean shouldRender(PlayerEntity player) {
        return shouldRender && (playerCondition == null || playerCondition.test(player));
    }

    public ConditionFactory<LivingEntity>.Instance getCondition() {
        return playerCondition;
    }
}
