package io.github.thatrobin.ccpacksapoli.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.thatrobin.ccpacksapoli.choice.Choice;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SelectableButtonWidget extends ButtonWidget {

    private final Identifier texture;
    private final int u;
    private final int v;
    private final int hoveredVOffset;
    private final int textureWidth;
    private final int textureHeight;
    private final ItemStack itemIcon;

    private final ItemRenderer itemRenderer;

    public Choice choice;

    public SelectableButtonWidget(Choice choice, ItemStack itemStack, ItemRenderer itemRenderer, int x, int y, int width, int height, Text text, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, PressAction onPress) {
        super(x, y, width, height, text, onPress, EMPTY);
        this.choice = choice;
        this.itemIcon = itemStack;
        this.itemRenderer = itemRenderer;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.u = u;
        this.v = v;
        this.hoveredVOffset = hoveredVOffset;
        this.texture = texture;
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);

        int i = this.v;
        if (this.isHovered()) {

            i += this.hoveredVOffset;
        }

        RenderSystem.enableDepthTest();
        drawTexture(matrices, this.x, this.y, (float)this.u, (float)i, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.isHovered()) {
            this.renderTooltip(matrices, mouseX, mouseY);
        }
        this.itemRenderer.renderInGui(itemIcon,x+4,y+4);

    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
    }

}
