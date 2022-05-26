package io.github.thatrobin.ccpacksapoli.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.thatrobin.ccpacksapoli.util.Sprite;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.client.gui.tooltip.BundleTooltipComponent.TEXTURE;

@Mixin(BundleTooltipComponent.class)
public abstract class BundleTooltipComponentMixin {

    @Shadow @Final private DefaultedList<ItemStack> inventory;

    @Shadow protected abstract int getColumns();

    @Shadow protected abstract int getRows();

    @Shadow @Final private int occupancy;

    @Shadow protected abstract void drawSlot(int x, int y, int index, boolean shouldBlock, TextRenderer textRenderer, MatrixStack matrices, ItemRenderer itemRenderer, int z);

    @Shadow protected abstract void drawOutline(int x, int y, int columns, int rows, MatrixStack matrices, int z);

    @Inject(method = "getColumns", at = @At("RETURN"), cancellable = true)
    public void getColumns(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int) Math.ceil(Math.sqrt(this.occupancy + 1.0D)));
    }

    @Inject(method = "getRows", at = @At("RETURN"), cancellable = true)
    public void getRows(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int) Math.ceil(Math.sqrt(this.occupancy + 1.0D)));
    }


    @Inject(method = "drawItems", at = @At("HEAD"), cancellable = true)
    private void getHeight(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, CallbackInfo ci) {
        //int i = getColumns();
        //int j = getRows();
        int columns = 5;
        int rows = (int)Math.max(1,Math.ceil((float)this.occupancy / (float)columns));
        int remainColumn = this.occupancy % columns;

        if(remainColumn != 0) {
            rows-=1;
        }

        int k = 0;

        for(int l = 0; l < rows; ++l) {
            for(int m = 0; m < columns; ++m) {
                int n = x + (m * 18) + 1;
                int o = y + (l * 20) + 1;
                this.drawSlotS(n, o, k++, textRenderer, matrices, itemRenderer, z);
            }
        }
        for(int l = 0; l < remainColumn; l++) {
            int n = x + (l * 18) + 1;
            int o = y + (rows * 20) + 1;
            this.drawSlotS(n, o, k++, textRenderer, matrices, itemRenderer, z);
        }

        //this.drawOutline(x, y, columns, rows, matrices, z);
        this.drawOutlineS(x, y, columns, remainColumn, rows, matrices, z);
        ci.cancel();
    }

    private void drawSlotS(int x, int y, int index, TextRenderer textRenderer, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
        this.drawS(matrices, x, y, z, Sprite.SLOT);
        if (index < this.inventory.size()) {
            ItemStack itemStack = this.inventory.get(index);
            itemRenderer.renderInGuiWithOverrides(itemStack, x + 1, y + 1, index);
            itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, x + 1, y + 1);
        }
    }

    private void drawOutlineS(int x, int y, int columns, int remainColumn, int rows, MatrixStack matrices, int z) {
        this.drawS(matrices, x, y, z, Sprite.BORDER_CORNER_TOP);
        if(this.occupancy < 5) {
            this.drawS(matrices, x + (remainColumn * 18) + 1, y, z, Sprite.BORDER_CORNER_TOP);
        } else {
            this.drawS(matrices, x + (columns * 18) + 1, y, z, Sprite.BORDER_CORNER_TOP);
        }


        int i;

        if(remainColumn != 0) {
            for (i = 0; i < columns; ++i) {
                if (i < remainColumn) {
                    this.drawS(matrices, x + 1 + (i * 18), y, z, Sprite.BORDER_HORIZONTAL_TOP);
                    this.drawS(matrices, x + 1 + (i * 18), y + (rows + 1) * 20, z, Sprite.BORDER_HORIZONTAL_BOTTOM);
                }
            }

        } else {
            for (i = 0; i < columns+1; ++i) {
                this.drawS(matrices, x + 1 + (i * 18), y, z, Sprite.BORDER_HORIZONTAL_TOP);
                if (i < columns) {
                    this.drawS(matrices, x + 1 + (i * 18), y + (rows * 20), z, Sprite.BORDER_HORIZONTAL_BOTTOM);
                }
            }
        }

        if(remainColumn != 0) {
            for (i = 0; i < rows+1; ++i) {
                this.drawS(matrices, x, y + i * 20 + 1, z, Sprite.BORDER_VERTICAL);
                if(i < rows) {
                    this.drawS(matrices, x + columns * 18 + 1, y + i * 20 + 1, z, Sprite.BORDER_VERTICAL);
                } else {
                    this.drawS(matrices, x + remainColumn * 18 + 1, y + i * 20 + 1, z, Sprite.BORDER_VERTICAL);
                }
            }
        } else {
            for(i = 0; i < rows; ++i) {
                this.drawS(matrices, x, y + i * 20 + 1, z, Sprite.BORDER_VERTICAL);
                this.drawS(matrices, x + columns * 18 + 1, y + i * 20 + 1, z, Sprite.BORDER_VERTICAL);
            }
        }


        if(this.occupancy < 5) {
            this.drawS(matrices, x, y + 20, z, Sprite.BORDER_CORNER_BOTTOM);
            this.drawS(matrices, x + (remainColumn * 18) + 1, y + 20, z, Sprite.BORDER_CORNER_BOTTOM);
        } else {
            this.drawS(matrices, x, y + rows * 20, z, Sprite.BORDER_CORNER_BOTTOM);
            this.drawS(matrices, x + columns * 18 + 1, y + rows * 20, z, Sprite.BORDER_CORNER_BOTTOM);
        }

    }

    private void drawS(MatrixStack matrices, int x, int y, int z, Sprite sprite) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        DrawableHelper.drawTexture(matrices, x, y, z, (float)sprite.u, (float)sprite.v, sprite.width, sprite.height, 128, 128);
    }

    //@Inject(method = "getWidth", at = @At("HEAD"), cancellable = true)
    //private void getWidth(CallbackInfoReturnable<Integer> cir) {
    //    if(this.inventory.size() == 1) {
    //        cir.setReturnValue(1);
    //    }
    //}

}
