package io.github.thatrobin.ra_additions_choices.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions_choices.choice.Choice;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceLayer;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceLayers;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceRegistry;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public interface ChoiceComponent extends AutoSyncedComponent {

    @SuppressWarnings("all")
    boolean hasChoice(ChoiceLayer layer);
    boolean hasAllChoices();

    HashMap<ChoiceLayer, Choice> getChoices();
    Choice getChoice(ChoiceLayer layer);

    @SuppressWarnings("unused")
    boolean hadChoiceBefore();

    void setChoice(ChoiceLayer layer, Choice choice);

    void sync();

    static void sync(PlayerEntity player) {
        ModComponents.CHOICE.sync(player);
        PowerHolderComponent.KEY.sync(player);
    }

    default void checkAutoChoosingLayers(PlayerEntity player) {
        ArrayList<ChoiceLayer> layers = new ArrayList<>();
        for(ChoiceLayer layer : ChoiceLayers.getLayers()) {
            if(layer.isEnabled()) {
                layers.add(layer);
            }
        }
        Collections.sort(layers);
        for(ChoiceLayer layer : layers) {
            boolean shouldContinue = false;
            if (layer.isEnabled() && !hasChoice(layer)) {
                if (layer.getChoiceOptionCount(player) == 1) {
                    List<Choice> choices = layer.getChoices(player).stream().map(ChoiceRegistry::get).toList();
                    setChoice(layer, choices.get(0));
                    shouldContinue = true;
                } else if(layer.getChoiceOptionCount(player) == 0) {
                    shouldContinue = true;
                }
            } else {
                shouldContinue = true;
            }
            if(!shouldContinue) {
                break;
            }
        }
    }
}
