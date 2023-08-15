package io.github.thatrobin.ra_additions_choices.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.thatrobin.ra_additions_choices.choice.Choice;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
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

    public Choice choice;

    public SelectableButtonWidget(Choice choice, ItemStack itemStack, ItemRenderer itemRenderer, int x, int y, int width, int height, Text text, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, PressAction onPress) {
        super(x, y, width, height, text, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.choice = choice;
        this.itemIcon = itemStack;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.u = u;
        this.v = v;
        this.hoveredVOffset = hoveredVOffset;
        this.texture = texture;
    }

    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, this.texture);

        int i = this.v;
        if (this.isHovered()) {

            i += this.hoveredVOffset;
        }

        RenderSystem.enableDepthTest();
        context.drawTexture(this.texture, this.getX(), this.getY(), (float)this.u, (float)i, this.width, this.height, this.textureWidth, this.textureHeight);
        //if (this.isHovered()) {
        //    context.drawTooltip().renderTooltip(matrices, mouseX, mouseY);
        //}
        context.drawItem(itemIcon, this.getX()+4, this.getY()+4);

    }

}
