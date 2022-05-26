package io.github.thatrobin.ccpacksapoli.util;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.HudRender;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class BossBarHudRender {

    public static final HudRender DONT_RENDER = new HudRender(false, 0, Apoli.identifier("textures/gui/resource_bar.png"), null, false);

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
        if(overlays != null) {
            return overlays;
        } else {
            return Lists.newArrayList();
        }
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
