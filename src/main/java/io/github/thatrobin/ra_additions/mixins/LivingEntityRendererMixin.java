package io.github.thatrobin.ra_additions.mixins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions.powers.AnimatedOverlayPower;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow protected M model;
    @Shadow protected abstract float getAnimationCounter(T entity, float tickDelta);

    @Shadow protected abstract float getHandSwingProgress(T entity, float tickDelta);

    @Shadow protected abstract float getAnimationProgress(T entity, float tickDelta);

    @Shadow protected abstract void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta);

    @Shadow protected abstract void scale(T entity, MatrixStack matrices, float amount);


    @Shadow protected abstract boolean isVisible(T entity);

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void addBloodOverlay(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(livingEntity, AnimatedOverlayPower.class)) {
            for (AnimatedOverlayPower power : PowerHolderComponent.getPowers(livingEntity, AnimatedOverlayPower.class)) {
                float n;
                Direction direction;
                matrixStack.push();
                this.model.handSwingProgress = this.getHandSwingProgress(livingEntity, g);
                this.model.riding = livingEntity.hasVehicle();
                this.model.child = livingEntity.isBaby();
                float h = MathHelper.lerpAngleDegrees(g, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
                float j = MathHelper.lerpAngleDegrees(g, livingEntity.prevHeadYaw, livingEntity.headYaw);
                float k = j - h;
                if (livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof LivingEntity livingEntity2) {
                    h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
                    k = j - h;
                    float l = MathHelper.wrapDegrees(k);
                    if (l < -85.0f) {
                        l = -85.0f;
                    }
                    if (l >= 85.0f) {
                        l = 85.0f;
                    }
                    h = j - l;
                    if (l * l > 2500.0f) {
                        h += l * 0.2f;
                    }
                    k = j - h;
                }
                float m = MathHelper.lerp(g, livingEntity.prevPitch, livingEntity.getPitch());
                if (LivingEntityRenderer.shouldFlipUpsideDown(livingEntity)) {
                    m *= -1.0f;
                    k *= -1.0f;
                }
                if (livingEntity.isInPose(EntityPose.SLEEPING) && (direction = livingEntity.getSleepingDirection()) != null) {
                    n = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1f;
                    matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0f, (float)(-direction.getOffsetZ()) * n);
                }
                float l = this.getAnimationProgress(livingEntity, g);
                this.setupTransforms(livingEntity, matrixStack, l, h, g);
                matrixStack.scale(-1.0f, -1.0f, 1.0f);
                this.scale(livingEntity, matrixStack, g);
                matrixStack.translate(0.0f, -1.501f, 0.0f);
                n = 0.0f;
                float o = 0.0f;
                if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
                    n = MathHelper.lerp(g, livingEntity.lastLimbDistance, livingEntity.limbDistance);
                    o = livingEntity.limbAngle - livingEntity.limbDistance * (1.0f - g);
                    if (livingEntity.isBaby()) {
                        o *= 3.0f;
                    }
                    if (n > 1.0f) {
                        n = 1.0f;
                    }
                }
                this.model.animateModel(livingEntity, o, n, g);
                this.model.setAngles(livingEntity, o, n, l, k, m);
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                boolean bl = this.isVisible(livingEntity);
                boolean bl2 = !bl && !(livingEntity).isInvisibleTo(minecraftClient.player);
                RenderLayer renderLayer = RenderLayer.getEntityTranslucent(power.getTexture());
                if (renderLayer != null) {
                    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
                    int p = LivingEntityRenderer.getOverlay(livingEntity, this.getAnimationCounter(livingEntity, g));
                    this.model.render(matrixStack, vertexConsumer, i, p, 1.0f, 1.0f, 1.0f, bl2 ? 0.15f : 1.0f);
                }
                matrixStack.pop();
            }
        }
    }
}
