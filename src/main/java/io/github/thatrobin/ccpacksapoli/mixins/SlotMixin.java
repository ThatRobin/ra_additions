package io.github.thatrobin.ccpacksapoli.mixins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ccpacksapoli.power.BindPower;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow public abstract ItemStack getStack();

    @Inject(method = "canTakeItems", at= @At("RETURN"), cancellable = true)
    public void canTakeItems(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> cir) {
        PowerHolderComponent.getPowers(playerEntity, BindPower.class).forEach(preventItemSelectionPower -> {
            if(preventItemSelectionPower.doesApply(this.getStack())) {
                if (preventItemSelectionPower.checkSlot(((Slot)(Object)this).getIndex())) {
                    cir.setReturnValue(false);
                }
            }
        });
    }
}
