package io.github.thatrobin.ra_additions.mixins;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.Factory;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.data.RAA_DataTypes;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Registry.class)
public interface RegistryMixin<T> {

    @Inject(method = "register(Lnet/minecraft/registry/Registry;Lnet/minecraft/util/Identifier;Ljava/lang/Object;)Ljava/lang/Object;", at = @At("HEAD"))
    private static <V, T extends V> void modifyEntry(Registry<V> registry, Identifier id, T entry, CallbackInfoReturnable<T> cir) {
        if(entry instanceof Factory) {
            boolean hasEntityAction = false;
            boolean hasBlockAction = false;
            for (String fieldName : ((Factory) entry).getSerializableData().getFieldNames()) {
                if(((Factory) entry).getSerializableData().getField(fieldName).getDataType().equals(ApoliDataTypes.ENTITY_ACTION)) {
                    hasEntityAction = true;
                }
                if(((Factory) entry).getSerializableData().getField(fieldName).getDataType().equals(ApoliDataTypes.BLOCK_ACTION)) {
                    hasBlockAction = true;
                }
            }
            if(hasEntityAction && hasBlockAction) {
                ((Factory) entry).getSerializableData().add("entityblock_action", RAA_DataTypes.ENTITYBLOCK_ACTION, null);
            }
        }
    }
}
