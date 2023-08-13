package io.github.thatrobin.ra_additions.mixins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.client.TestArmorItem;
import io.github.thatrobin.ra_additions.powers.CustomModelRenderPower;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.animatable.client.RenderProvider;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {

    @Shadow protected abstract void setVisible(A bipedModel, EquipmentSlot slot);

    @Shadow protected abstract boolean usesInnerModel(EquipmentSlot slot);

    @Shadow protected abstract void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean glint, A model, boolean secondTextureLayer, float red, float green, float blue, @Nullable String overlay);

    @Shadow protected abstract A getModel(EquipmentSlot slot);

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"))
    private void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if(PowerHolderComponent.hasPower(livingEntity, CustomModelRenderPower.class)) {
            for (CustomModelRenderPower<?> power : PowerHolderComponent.getPowers(livingEntity, CustomModelRenderPower.class)) {
                if (power.isActive()) {
                    try {
                        for (Pair<EquipmentSlot, TestArmorItem> item : power.getItems()) {
                            this.renderArmor(power, matrixStack, vertexConsumerProvider, livingEntity, item.getRight().getDefaultStack(), item.getLeft(), i, this.getModel(item.getLeft()));
                        }
                    } catch (GeckoLibException ignored) {
                    }
                }
            }
        }
    }

    private void renderArmor(CustomModelRenderPower<?> power, MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, ItemStack itemStack, EquipmentSlot armorSlot, int light, A model) {
        ArmorFeatureRenderer<T,M,A> renderer = (ArmorFeatureRenderer<T,M,A>)(Object)this;
        if (!(itemStack.getItem() instanceof ArmorItem)) {
            return;
        }
        ArmorItem armorItem = (ArmorItem)itemStack.getItem();
        if (armorItem.getSlotType() != armorSlot) {
            return;
        }
        renderer.getContextModel().copyBipedStateTo(model);
        this.setVisible(model, armorSlot);
        boolean bl = this.usesInnerModel(armorSlot);
        boolean bl2 = itemStack.hasGlint();
        itemStack.setCustomName(Text.literal(power.getPath().toString()));
        A renderModel = (A) RenderProvider.of(itemStack).getGenericArmorModel(entity, itemStack, armorSlot, (BipedEntityModel<LivingEntity>) model);
        this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, renderModel, bl, 1.0f, 1.0f, 1.0f, null);
    }
}
