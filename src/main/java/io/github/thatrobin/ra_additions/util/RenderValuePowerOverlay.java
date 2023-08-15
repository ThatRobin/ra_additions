package io.github.thatrobin.ra_additions.util;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions.powers.ValuePower;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class RenderValuePowerOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        if (MinecraftClient.getInstance().player != null) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if(PowerHolderComponent.hasPower(player, ValuePower.class)) {
                List<ValuePower> powers = PowerHolderComponent.getPowers(player, ValuePower.class);
                for (ValuePower power : powers) {
                    context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, power.getOrCreateValueTranslationKey(), power.getPosX(), power.getPosY(), 0xFFFFFF);
                }
            }
        }
    }

}
