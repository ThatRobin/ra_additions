package io.github.thatrobin.ra_additions_tags.mixin;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.origin.Origin;
import io.github.thatrobin.ra_additions_tags.data_loaders.PowerTagManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.List;

@Pseudo
@Mixin(Origin.class)
public class OriginMixin {

    @SuppressWarnings("all")
    @Inject(method = "createFromData", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void grabPowerTags(Identifier id, SerializableData.Instance data, CallbackInfoReturnable<Origin> cir, Origin origin) {
        ((List<Identifier>)data.get("powers")).forEach(powerId -> {
            try {
                PowerType<?> powerType = PowerTypeRegistry.get(powerId);
                origin.add(powerType);
            } catch(IllegalArgumentException e) {
                Origins.LOGGER.error("Origin \"" + id + "\" contained unregistered power: \"" + powerId + "\"");
            }
        });

        Collection<PowerType<?>> tag = PowerTagManager.POWER_TAG_LOADER.getTagOrEmpty(id);
        if(tag.size() > 0) {
            for (PowerType<?> powerType : tag) {
                origin.add(powerType);
            }
        }
        cir.setReturnValue(origin);
    }
}