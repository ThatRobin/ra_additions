package io.github.thatrobin.ra_additions.mixins;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@Mixin(value = GeoArmorRenderer.class, remap = false)
public interface GeoArmorRendererAccessor<T extends Item & GeoItem> {

    @Mutable
    @Accessor("model")
    GeoModel<T> getModel();

    @Mutable
    @Accessor("model")
    void setModel(GeoModel<T> model);
}
