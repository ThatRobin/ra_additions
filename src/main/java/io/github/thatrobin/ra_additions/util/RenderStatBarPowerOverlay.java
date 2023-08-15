package io.github.thatrobin.ra_additions.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions.powers.StatBarPower;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;

public class RenderStatBarPowerOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        if (MinecraftClient.getInstance().player != null) {
            PlayerEntity playerEntity = MinecraftClient.getInstance().player;

            int leftBars = 0;
            int rightBars = 0;

            int airMax = playerEntity.getMaxAir();
            int cappedAir = Math.min(playerEntity.getAir(), airMax);

            int scaledHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
            int scaledWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();

            for (int bar = 0; bar < PowerHolderComponent.getPowers(playerEntity, StatBarPower.class).size(); bar++) {
                StatBarPower statBarPower = PowerHolderComponent.getPowers(playerEntity, StatBarPower.class).get(bar);

                int baseBars = 0;

                if (statBarPower.getHudRender().getSide().equals("left")) {
                    int hasArmor = playerEntity.getArmor() > 0 ? 1 : 0;
                    baseBars = 1 + hasArmor + leftBars;
                } else if (statBarPower.getHudRender().getSide().equals("right")) {
                    int isUnderwater = playerEntity.isSubmergedIn(FluidTags.WATER) || cappedAir < airMax ? 1 : 0;
                    baseBars = 1 + isUnderwater + rightBars;
                }

                RenderSystem.setShaderTexture(0, statBarPower.getHudRender().getSpriteLocation());


                int scaledScaledHeight = scaledHeight - 39;
                int oscaledScaledWidth = scaledWidth / 2 + 90;
                int ascaledScaledWidth = scaledWidth / 2 - 10;

                MinecraftClient.getInstance().getProfiler().swap("air");


                int oorigY = scaledScaledHeight - (baseBars * 10);
                int aorigY = scaledScaledHeight - (baseBars * 10);

                int armorToughness = statBarPower.getValue();
                if (statBarPower.getHudRender().shouldRender(playerEntity) && armorToughness > 0) {
                    int aorigX;
                    int oorigX;
                    int index = statBarPower.getHudRender().getBarIndex();
                    switch (statBarPower.getHudRender().getSide()) {
                        case "left" -> {
                            leftBars += 1;
                            for (int i = 0; i < 10; i++) {
                                aorigX = ascaledScaledWidth - (10 - i) * 8;
                                if (i * 2 + 1 < armorToughness) {
                                    context.drawTexture(statBarPower.getHudRender().getSpriteLocation(), aorigX, aorigY, 27, index * 9, 9, 9);
                                }

                                if (i * 2 + 1 == armorToughness) {
                                    context.drawTexture(statBarPower.getHudRender().getSpriteLocation(), aorigX, aorigY, 9, index * 9, 9, 9);
                                }

                                if (i * 2 + 1 > armorToughness) {
                                    context.drawTexture(statBarPower.getHudRender().getSpriteLocation(), aorigX, aorigY, 0, index * 9, 9, 9);
                                }
                            }
                        }
                        case "right" -> {
                            rightBars += 1;
                            armorToughness += 2;
                            for (int i = 10; i > 0; i--) {
                                oorigX = oscaledScaledWidth - i * 8;
                                if (i * 2 + 1 < armorToughness) {
                                    context.drawTexture(statBarPower.getHudRender().getSpriteLocation(), oorigX, oorigY, 27, index * 9, 9, 9);
                                }

                                if (i * 2 + 1 == armorToughness) {
                                    context.drawTexture(statBarPower.getHudRender().getSpriteLocation(), oorigX, oorigY, 18, index * 9, 9, 9);
                                }

                                if (i * 2 + 1 > armorToughness) {
                                    context.drawTexture(statBarPower.getHudRender().getSpriteLocation(), oorigX, oorigY, 0, index * 9, 9, 9);
                                }
                            }
                        }
                    }

                    MinecraftClient.getInstance().getProfiler().pop();
                }
            }
        }
    }
}
