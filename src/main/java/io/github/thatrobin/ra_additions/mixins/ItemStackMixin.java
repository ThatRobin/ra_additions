package io.github.thatrobin.ra_additions.mixins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions.powers.BindPower;
import io.github.thatrobin.ra_additions.powers.ItemUsePower;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 999)
public abstract class ItemStackMixin {

    @Inject(method = "use", at = @At("HEAD"))
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        PowerHolderComponent.getPowers(user, ItemUsePower.class).forEach(power -> {
            if (power.doesApply((ItemStack) (Object) this)) {
                power.executeActions((ItemStack) (Object) this);
            }
        });
    }

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    public void preventUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
        if(user != null) {
            ItemStack stackInHand = user.getStackInHand(hand);
            PowerHolderComponent.getPowers(user, BindPower.class).forEach(bindPower -> {
                if(bindPower.doesApply(stackInHand)) {
                    if (bindPower.checkSlot((user.getInventory().getSlotWithStack(stackInHand)))) {
                        if(bindPower.doesPrevent(stackInHand)) {
                            info.setReturnValue(TypedActionResult.fail(stackInHand));
                        }
                    }
                }
            });
        }
    }
}
