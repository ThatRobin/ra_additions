package io.github.thatrobin.ra_additions_choices.component;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_choices.choice.Choice;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceLayer;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceLayers;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerChoiceComponent implements ChoiceComponent {

    private final PlayerEntity player;
    private final HashMap<ChoiceLayer, Choice> choices = new HashMap<>();

    private boolean hadChoiceBefore = false;

    public PlayerChoiceComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public boolean hasAllChoices() {
        return ChoiceLayers.getLayers().stream().allMatch(layer -> !layer.isEnabled() || layer.getChoices(player).size() == 0 || (choices.containsKey(layer) && choices.get(layer) != null && choices.get(layer) != Choice.EMPTY));
    }

    @Override
    public HashMap<ChoiceLayer, Choice> getChoices() {
        return choices;
    }

    @Override
    public boolean hasChoice(ChoiceLayer layer) {
        return !choices.isEmpty() && choices.containsKey(layer) && choices.get(layer) != null && choices.get(layer) != Choice.EMPTY;
    }

    @Override
    public Choice getChoice(ChoiceLayer layer) {
        if(!choices.containsKey(layer)) {
            return null;
        }
        return choices.get(layer);
    }

    @Override
    public boolean hadChoiceBefore() {
        return hadChoiceBefore;
    }

    @Override
    public void setChoice(ChoiceLayer layer, Choice choice) {
        Choice oldOrigin = getChoice(layer);
        if(oldOrigin == choice) {
            return;
        }
        this.choices.put(layer, choice);
        PowerHolderComponent powerComponent = PowerHolderComponent.KEY.get(player);
        grantPowersFromChoice(choice, powerComponent);
        if(oldOrigin != null) {
            powerComponent.removeAllPowersFromSource(oldOrigin.getIdentifier());
        }
        if(this.hasAllChoices()) {
            this.hadChoiceBefore = true;
        }
    }

    private void grantPowersFromChoice(Choice choice, PowerHolderComponent powerComponent) {
        Identifier source = choice.getIdentifier();
        for(PowerType<?> powerType : choice.getPowerTypes()) {
            if(!powerComponent.hasPower(powerType, source)) {
                powerComponent.addPower(powerType, source);
            }
        }
    }

    private void revokeRemovedPowers(Choice choice, PowerHolderComponent powerComponent) {
        Identifier source = choice.getIdentifier();
        List<PowerType<?>> powersByOrigin = powerComponent.getPowersFromSource(source);
        powersByOrigin.stream().filter(p -> !choice.hasPowerType(p)).forEach(p -> powerComponent.removePower(p, source));
    }

    @SuppressWarnings("all")
    @Override
    public void readFromNbt(@NotNull NbtCompound compoundTag) {
        if (player == null) {
            RA_Additions.LOGGER.error("Player was null in `fromTag`! This is a bug!");
        }

        this.choices.clear();

        if (compoundTag.contains("Choice")) {
            try {
                ChoiceLayer defaultChoiceLayer = ChoiceLayers.getLayer(RA_Additions.identifier("choice"));
                this.choices.put(defaultChoiceLayer, ChoiceRegistry.get(Identifier.tryParse(compoundTag.getString("Choice"))));
            } catch (IllegalArgumentException e) {
                RA_Additions.LOGGER.warn("Player " + player.getDisplayName().getString() + " had old choice which could not be migrated: " + compoundTag.getString("Choice"));
            }
        } else {
            NbtList choiceLayerList = (NbtList) compoundTag.get("ChoiceLayers");
            if (choiceLayerList != null) {
                for (int i = 0; i < choiceLayerList.size(); i++) {
                    NbtCompound layerTag = choiceLayerList.getCompound(i);
                    Identifier layerId = Identifier.tryParse(layerTag.getString("Layer"));
                    ChoiceLayer layer = null;
                    try {
                        layer = ChoiceLayers.getLayer(layerId);
                    } catch (IllegalArgumentException e) {
                        RA_Additions.LOGGER.warn("Could not find choice layer with id " + layerId.toString() + ", which existed on the data of player " + player.getDisplayName().getString() + ".");
                    }
                    if (layer != null) {
                        Identifier choiceId = Identifier.tryParse(layerTag.getString("Choice"));
                        Choice choice = null;
                        try {
                            choice = ChoiceRegistry.get(choiceId);
                        } catch (IllegalArgumentException e) {
                            RA_Additions.LOGGER.warn("Could not find choice with id " + choiceId + ", which existed on the data of player " + player.getDisplayName().getString() + ".");
                            PowerHolderComponent powerComponent = PowerHolderComponent.KEY.get(player);
                            powerComponent.removeAllPowersFromSource(choiceId);
                        }
                        if (choice != null) {
                            if (!layer.contains(choice) && !choice.isSpecial()) {
                                RA_Additions.LOGGER.warn("Choice with id " + choice.getIdentifier().toString() + " is not in layer " + layer.getIdentifier().toString() + ", but was found on " + player.getDisplayName().getString() + ", setting to EMPTY.");
                                choice = Choice.EMPTY;
                                PowerHolderComponent powerComponent = PowerHolderComponent.KEY.get(player);
                                powerComponent.removeAllPowersFromSource(choiceId);
                            }
                            this.choices.put(layer, choice);
                        }
                    }
                }
            }
        }
        this.hadChoiceBefore = compoundTag.getBoolean("HadChoiceBefore");

        if (!player.getWorld().isClient) {
            PowerHolderComponent powerHolderComponent = PowerHolderComponent.KEY.get(player);
            for (Choice choice : choices.values()) {
                // Grants powers only if the player doesn't have them yet from the specific Origin source.
                // Needed in case the origin was set before the update to Apoli happened.
                grantPowersFromChoice(choice, powerHolderComponent);
            }
            for (Choice origin : choices.values()) {
                revokeRemovedPowers(origin, powerHolderComponent);
            }

            // Compatibility with old worlds:
            // Loads power data from Origins tag, whereas new versions
            // store the data in the Apoli tag.
            if (compoundTag.contains("Powers")) {
                NbtList powerList = (NbtList) compoundTag.get("Powers");
                for (int i = 0; i < Objects.requireNonNull(powerList).size(); i++) {
                    NbtCompound powerTag = powerList.getCompound(i);
                    Identifier powerTypeId = Identifier.tryParse(powerTag.getString("Type"));
                    try {
                        PowerType<?> type = PowerTypeRegistry.get(powerTypeId);
                        if (powerHolderComponent.hasPower(type)) {
                            NbtElement data = powerTag.get("Data");
                            try {
                                powerHolderComponent.getPower(type).fromTag(data);
                            } catch (ClassCastException e) {
                                // Occurs when power was overriden by data pack since last world load
                                // to be a power type which uses different data class.
                                RA_Additions.LOGGER.warn("Data type of \"" + powerTypeId + "\" changed, skipping data for that power on player " + player.getName().getString());
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        RA_Additions.LOGGER.warn("Power data of unregistered power \"" + powerTypeId + "\" found on player, skipping...");
                    }
                }
            }
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound compoundTag) {
        NbtList choiceLayerList = new NbtList();
        for(Map.Entry<ChoiceLayer, Choice> entry : choices.entrySet()) {
            NbtCompound layerTag = new NbtCompound();
            if(entry.getKey() != null) {
                layerTag.putString("Layer", entry.getKey().getIdentifier().toString());
            }
            if(entry.getValue() != null) {
                layerTag.putString("Choice", entry.getValue().getIdentifier().toString());
            }
            choiceLayerList.add(layerTag);
        }
        compoundTag.put("ChoiceLayers", choiceLayerList);
        compoundTag.putBoolean("HadChoiceBefore", this.hadChoiceBefore);
    }

    @Override
    public void sync() {
        ChoiceComponent.sync(this.player);
    }
}
