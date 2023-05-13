package io.github.thatrobin.ra_additions.client;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.thatrobin.ra_additions.powers.CustomModelRenderPower;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class TestArmorRenderer<T extends Item & GeoItem> extends GeoArmorRenderer<T> {

    public TestArmorRenderer(DefaultedItemGeoModel<T> model) {
        super(model);
    }

    @Override
    public void actuallyRender(MatrixStack poseStack, T animatable, BakedGeoModel model, RenderLayer renderType,
                               VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if(this.currentEntity != null) {
            if(!PowerHolderComponent.getPowers(this.currentEntity, CustomModelRenderPower.class).isEmpty()) {
                if(PowerHolderComponent.getPowers(this.currentEntity, CustomModelRenderPower.class).stream().anyMatch(Power::isActive)) {
                    super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
                }
            }
        }
    }

}