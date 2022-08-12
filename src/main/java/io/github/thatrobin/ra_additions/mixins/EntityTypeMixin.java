package io.github.thatrobin.ra_additions.mixins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions.util.UniversalPower;
import io.github.thatrobin.ra_additions.util.UniversalPowerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin<T extends Entity> {

    @Shadow @Final private EntityType.EntityFactory<T> factory;

    @SuppressWarnings("unchecked")
    @Inject(method = "create(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;", at = @At("HEAD"), cancellable = true)
    public void create(World world, CallbackInfoReturnable<T> cir) {
        T entity = this.factory.create((EntityType<T>) (Object)this, world);
        if(entity instanceof LivingEntity livingEntity) {
            UniversalPowerRegistry.entries().forEach((identifierUniversalPowerEntry -> {
                Identifier id = identifierUniversalPowerEntry.getKey();
                UniversalPower up = identifierUniversalPowerEntry.getValue();
                if(up.entities.contains((EntityType<T>) (Object)this)) {
                    up.powerTypes.forEach(powerType -> PowerHolderComponent.KEY.get(livingEntity).addPower(powerType, id));
                }
            }));

        }
        cir.setReturnValue(entity);
    }
}
