package io.github.thatrobin.ccpacksapoli.mixins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ccpacksapoli.power.ActionOnProjectileLand;
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

    @Inject(method = "onCollision", at = @At(value = "HEAD"))
    public void onCollision(HitResult hitResult, CallbackInfo ci) {
        PowerHolderComponent.withPower(this.getOwner(), ActionOnProjectileLand.class, null, actionOnProjectileLand -> {
            EntityType<?> projectile = actionOnProjectileLand.getProjectile();
            if(((ProjectileEntity)(Object)this).getType() == projectile || projectile == null) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult) hitResult;

                    if (actionOnProjectileLand.doesApply(blockHitResult.getBlockPos())) {
                        actionOnProjectileLand.executeActions(blockHitResult.getBlockPos(), Direction.UP, ((ProjectileEntity)(Object)this));
                    }
                } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult entityHitResult = (EntityHitResult) hitResult;

                    if (actionOnProjectileLand.doesApply(entityHitResult.getEntity().getLandingPos())) {
                        actionOnProjectileLand.executeActions(entityHitResult.getEntity().getLandingPos(), Direction.UP, ((ProjectileEntity)(Object)this));
                    }
                }
            }
        });
    }
}
