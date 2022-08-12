package io.github.thatrobin.ra_additions.mixins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions.powers.BindPower;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrameEntity.class)
public class ItemFrameEntityMixin {

    @Inject(
            method = "interact",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void preventInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        PowerHolderComponent.getPowers(player, BindPower.class).forEach(preventItemSelectionPower -> {
            if(preventItemSelectionPower.doesApply(itemStack)) {
                if (preventItemSelectionPower.checkSlot((player.getInventory().getSlotWithStack(itemStack)))) {
                    cir.setReturnValue(ActionResult.FAIL);
                }
            }
        });
    }

}