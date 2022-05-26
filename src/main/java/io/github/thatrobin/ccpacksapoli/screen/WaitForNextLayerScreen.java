package io.github.thatrobin.ccpacksapoli.screen;

import io.github.thatrobin.ccpacksapoli.choice.ChoiceLayer;
import io.github.thatrobin.ccpacksapoli.component.ChoiceComponent;
import io.github.thatrobin.ccpacksapoli.component.ModComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;

public class WaitForNextLayerScreen extends Screen {

    private final ArrayList<ChoiceLayer> layerList;
    private final int currentLayerIndex;
    private final boolean showDirtBackground;
    private final int maxSelection;

    protected WaitForNextLayerScreen(ArrayList<ChoiceLayer> layerList, int currentLayerIndex, boolean showDirtBackground) {
        super(LiteralText.EMPTY);
        this.layerList = layerList;
        this.currentLayerIndex = currentLayerIndex;
        this.showDirtBackground = showDirtBackground;
        ChoiceLayer currentLayer = layerList.get(currentLayerIndex);
        maxSelection = currentLayer.getChoiceOptionCount(MinecraftClient.getInstance().player);
    }

    public void openSelection() {
        int index = currentLayerIndex + 1;
        PlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        ChoiceComponent component = ModComponents.CHOICE.get(player);

        while(index < layerList.size()) {
            if(!component.hasChoice(layerList.get(index)) && layerList.get(index).getChoices(MinecraftClient.getInstance().player).size() > 0) {
                MinecraftClient.getInstance().setScreen(new ChooseChoiceScreen(layerList, index, showDirtBackground));
                return;
            }
            index++;
        }
        MinecraftClient.getInstance().setScreen(null);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(maxSelection == 0) {
            openSelection();
            return;
        }
        this.renderBackground(matrices);
    }

    @Override
    public void renderBackground(MatrixStack matrices, int vOffset) {
        if(showDirtBackground) {
            super.renderBackgroundTexture(vOffset);
        } else {
            super.renderBackground(matrices, vOffset);
        }
    }
}