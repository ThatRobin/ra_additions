package io.github.thatrobin.ra_additions.mixins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions.powers.ActionOnProjectileLand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {

    @Shadow @Nullable public abstract Entity getOwner();

    @Inject(method = "onCollision", at = @At(value = "HEAD"), cancellable = true)
    public void onCollision(HitResult hitResult, CallbackInfo ci) {
        boolean shouldDamage = true;
        for (ActionOnProjectileLand actionOnProjectileLand : PowerHolderComponent.getPowers(this.getOwner(), ActionOnProjectileLand.class)) {
            EntityType<?> projectile = actionOnProjectileLand.getProjectile();
            if(((ProjectileEntity)(Object)this).getType() == projectile || actionOnProjectileLand.getProjectileId() == null) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult) hitResult;

                    if (actionOnProjectileLand.doesApplyBlock(blockHitResult.getBlockPos())) {
                        actionOnProjectileLand.executeBlockAction(blockHitResult.getBlockPos(), Direction.UP);
                    }
                } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult entityHitResult = (EntityHitResult) hitResult;

                    if (actionOnProjectileLand.doesApplyEntity(entityHitResult.getEntity())) {
                        actionOnProjectileLand.executeEntityAction(entityHitResult.getEntity());
                    }
                }
            }
            if(!actionOnProjectileLand.shouldDamage()) shouldDamage = false;
        }
        if(!shouldDamage) ci.cancel();
    }
}
