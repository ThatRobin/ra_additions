package io.github.thatrobin.ra_additions.mixins;

import com.google.common.collect.ImmutableList;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions.powers.BorderPower;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getWorldBorder()Lnet/minecraft/world/border/WorldBorder;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void borderPower(Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions, CallbackInfoReturnable<Vec3d> cir, ImmutableList.Builder<VoxelShape> builder) {
        if(entity != null) {
            if (world.isClient) {
                Iterable<Entity> entities = ((ClientWorld) world).getEntities();
                for (Entity entity1 : entities) {
                    if (entity1 instanceof LivingEntity livingEntity) {
                        PowerHolderComponent component = PowerHolderComponent.KEY.get(livingEntity);
                        for (BorderPower borderPower : component.getPowers(BorderPower.class)) {
                            boolean bl = borderPower.canCollide(entity, entityBoundingBox.stretch(movement));
                            if (bl && !borderPower.doesApply(entity)) {
                                builder.add(borderPower.getCollidingShape(entity));
                            }
                        }
                    }
                }
            } else {
                List<? extends LivingEntity> entities = ((ServerWorld) world).getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), (entityVar) -> PowerHolderComponent.hasPower(entityVar, BorderPower.class));
                for (LivingEntity entity1 : entities) {
                    PowerHolderComponent component = PowerHolderComponent.KEY.get(entity1);
                    for (BorderPower borderPower : component.getPowers(BorderPower.class)) {
                        boolean bl = borderPower.canCollide(entity, entityBoundingBox.stretch(movement));
                        if (bl && !borderPower.doesApply(entity)) {
                            builder.add(borderPower.getCollidingShape(entity));
                        }
                    }
                }
            }
        }
    }

}
