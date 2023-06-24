package io.github.thatrobin.ra_additions_tags.mixin;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.action.ActionType;
import io.github.apace100.apoli.power.factory.action.ActionTypes;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.thatrobin.ra_additions_tags.registries.BiEntityActionRegistry;
import io.github.thatrobin.ra_additions_tags.registries.BlockActionRegistry;
import io.github.thatrobin.ra_additions_tags.registries.EntityActionRegistry;
import io.github.thatrobin.ra_additions_tags.registries.ItemActionRegistry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ApoliDataTypes.class, remap = false)
public class ApoliDataTypesMixin {

    @Inject(method = "action", at = @At("RETURN"), cancellable = true)
    private static <T> void action(Class<ActionFactory<T>.Instance> dataClass, ActionType<T> actionType, CallbackInfoReturnable<SerializableDataType<ActionFactory<T>.Instance>> cir) {
        cir.setReturnValue(new SerializableDataType<>(dataClass, actionType::write, actionType::read, jsonElement -> {
            if(jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                String idStr = jsonElement.getAsString();
                if (actionType == ActionTypes.ENTITY) {
                    return EntityActionRegistry.get(Identifier.tryParse(idStr)).getAction();
                } else if (actionType.equals(ActionTypes.BIENTITY)) {
                    return BiEntityActionRegistry.get(Identifier.tryParse(idStr)).getAction();
                } else if (actionType.equals(ActionTypes.BLOCK)) {
                    return BlockActionRegistry.get(Identifier.tryParse(idStr)).getAction();
                } else if (actionType.equals(ActionTypes.ITEM)) {
                    return ItemActionRegistry.get(Identifier.tryParse(idStr)).getAction();
                }
            }
            return actionType.read(jsonElement);
        }));
    }
}
