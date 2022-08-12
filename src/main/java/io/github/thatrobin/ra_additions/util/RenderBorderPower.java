package io.github.thatrobin.ra_additions.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.thatrobin.ra_additions.powers.BorderPower;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class RenderBorderPower {

    public static void renderWorldBorder(Camera camera, BorderPower border) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        double d = MinecraftClient.getInstance().options.getClampedViewDistance() * 16;
        if (!(camera.getPos().x < border.getBoundEast() - d) || !(camera.getPos().x > border.getBoundWest() + d) || !(camera.getPos().z < border.getBoundSouth() - d) || !(camera.getPos().z > border.getBoundNorth() + d)) {
            double f = camera.getPos().x;
            double g = camera.getPos().z;
            double h = MinecraftClient.getInstance().gameRenderer.method_32796();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, border.getTexture());
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setShaderColor(border.red, border.green, border.blue, border.alpha);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.polygonOffset(-3.0F, -3.0F);
            RenderSystem.enablePolygonOffset();
            RenderSystem.disableCull();
            float m = 0.0f;
            float p = (float)(h - camera.getPos().y);
            if(border.scrollTexture) {
                m = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0F;
            }
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            double q = Math.max(g - d, border.getBoundNorth());
            double r = Math.min(g + d, border.getBoundSouth());
            float v;
            float s;
            double t;
            double u;
            if (f > border.getBoundEast() - d) {
                s = 0.0F;
                for(t = q; t < r; s += 0.5F) {
                    u = Math.min(1.0D, r - t);
                    v = (float)u * 0.5F;
                    bufferBuilder.vertex(border.getBoundEast() - f, -h, t - g).texture(m - s, m + p).next();
                    bufferBuilder.vertex(border.getBoundEast() - f, -h, t + u - g).texture(m - (v + s), m + p).next();
                    bufferBuilder.vertex(border.getBoundEast() - f, h, t + u - g).texture(m - (v + s), m + 0.0F).next();
                    bufferBuilder.vertex(border.getBoundEast() - f, h, t - g).texture(m - s, m + 0.0F).next();
                    ++t;
                }
            }
            if (f < border.getBoundWest() + d) {
                s = 0.0F;
                for (t = q; t < r; s += 0.5F) {
                    u = Math.min(1.0D, r - t);
                    v = (float) u * 0.5F;
                    bufferBuilder.vertex(border.getBoundWest() - f, -h, t - g).texture(m + s, m + p).next();
                    bufferBuilder.vertex(border.getBoundWest() - f, -h, t + u - g).texture(m + v + s, m + p).next();
                    bufferBuilder.vertex(border.getBoundWest() - f, h, t + u - g).texture(m + v + s, m + 0.0F).next();
                    bufferBuilder.vertex(border.getBoundWest() - f, h, t - g).texture(m + s, m + 0.0F).next();
                    ++t;
                }
            }
            q = Math.max(MathHelper.floor(f - d), border.getBoundWest());
            r = Math.min(MathHelper.ceil(f + d), border.getBoundEast());
            if (g > border.getBoundSouth() - d) {
                s = 0.0F;
                for(t = q; t < r; s += 0.5F) {
                    u = Math.min(1.0D, r - t);
                    v = (float)u * 0.5F;
                    bufferBuilder.vertex(t - f, -h, border.getBoundSouth() - g).texture(m + s, m + p).next();
                    bufferBuilder.vertex(t + u - f, -h, border.getBoundSouth() - g).texture(m + v + s, m + p).next();
                    bufferBuilder.vertex(t + u - f, h, border.getBoundSouth() - g).texture(m + v + s, m + 0.0F).next();
                    bufferBuilder.vertex(t - f, h, border.getBoundSouth() - g).texture(m + s, m + 0.0F).next();
                    ++t;
                }
            }
            if (g < border.getBoundNorth() + d) {
                s = 0.0F;
                for(t = q; t < r; s += 0.5F) {
                    u = Math.min(1.0D, r - t);
                    v = (float)u * 0.5F;
                    bufferBuilder.vertex(t - f, -h, border.getBoundNorth() - g).texture(m - s, m + p).next();
                    bufferBuilder.vertex(t + u - f, -h, border.getBoundNorth() - g).texture(m - (v + s), m + p).next();
                    bufferBuilder.vertex(t + u - f, h, border.getBoundNorth() - g).texture(m - (v + s), m + 0.0F).next();
                    bufferBuilder.vertex(t - f, h, border.getBoundNorth() - g).texture(m - s, m + 0.0F).next();
                    ++t;
                }
            }
            BufferRenderer.drawWithShader(bufferBuilder.end());
            RenderSystem.enableCull();
            RenderSystem.polygonOffset(0.0F, 0.0F);
            RenderSystem.disablePolygonOffset();
            RenderSystem.disableBlend();
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.depthMask(true);
        }
    }
}
