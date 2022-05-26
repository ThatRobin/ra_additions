package io.github.thatrobin.ccpacksapoli.choice;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.MultiplePowerType;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ccpacksapoli.CCPacksApoli;
import io.github.thatrobin.ccpacksapoli.component.ModComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Choice {

    public static final SerializableData DATA = new SerializableData()
            .add("powers", SerializableDataTypes.IDENTIFIERS, Lists.newArrayList())
            .add("name", SerializableDataTypes.STRING, "")
            .add("icon", SerializableDataTypes.ITEM_STACK, new ItemStack(Items.AIR))
            .add("action_on_chosen", ApoliDataTypes.ENTITY_ACTION, null)
            .add("description", SerializableDataTypes.STRING, "");

    public static final Choice EMPTY;

    static {
        EMPTY = register(new Choice(CCPacksApoli.identifier("empty")));
    }

    public static void init() {
    }

    private static Choice register(Choice choice) {
        return ChoiceRegistry.register(choice);
    }

    public static HashMap<ChoiceLayer, Choice> get(Entity entity) {
        if(entity instanceof PlayerEntity) {
            return get((PlayerEntity)entity);
        }
        return new HashMap<>();
    }

    public static HashMap<ChoiceLayer, Choice> get(PlayerEntity player) {
        return ModComponents.CHOICE.get(player).getChoices();
    }

    private final Identifier identifier;
    private ItemStack itemIcon;
    private ActionFactory<Entity>.Instance action;
    private final List<PowerType<?>> powerTypes = new LinkedList<>();

    private String nameTranslationKey;
    private String descriptionTranslationKey;

    public Choice(Identifier id) {
        this.identifier = id;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Choice add(PowerType<?>... powerTypes) {
        this.powerTypes.addAll(Lists.newArrayList(powerTypes));
        return this;
    }

    public Choice setName(String name) {
        this.nameTranslationKey = name;
        return this;
    }

    public void setIcon(ItemStack icon) {
        this.itemIcon = icon;
    }

    public void setAction(ActionFactory<Entity>.Instance action) {
        this.action = action;
    }

    public void setDescription(String description) {
        this.descriptionTranslationKey = description;
    }

    public boolean hasPowerType(PowerType<?> powerType) {
        if(powerType.getIdentifier() == null) {
            return false;
        }
        if(this.powerTypes.contains(powerType)) {
            return true;
        }
        for (PowerType<?> pt : this.powerTypes) {
            if (pt instanceof MultiplePowerType) {
                if(((MultiplePowerType<?>)pt).getSubPowers().contains(powerType.getIdentifier())) {
                    return true;
                }
            }
        }
        return false;
    }


    public Iterable<PowerType<?>> getPowerTypes() {
        return powerTypes;
    }

    public ItemStack getIcon() {
        return itemIcon;
    }

    public ActionFactory<Entity>.Instance getAction() {
        return this.action;
    }

    public String getOrCreateNameTranslationKey() {
        if(nameTranslationKey == null || nameTranslationKey.isEmpty()) {
            nameTranslationKey =
                    "choice." + identifier.getNamespace() + "." + identifier.getPath() + ".name";
        }
        return nameTranslationKey;
    }

    public TranslatableText getName() {
        return new TranslatableText(getOrCreateNameTranslationKey());
    }

    public String getOrCreateDescriptionTranslationKey() {
        if(descriptionTranslationKey == null || descriptionTranslationKey.isEmpty()) {
            descriptionTranslationKey =
                    "choice." + identifier.getNamespace() + "." + identifier.getPath() + ".description";
        }
        return descriptionTranslationKey;
    }

    public TranslatableText getDescription() {
        return new TranslatableText(getOrCreateDescriptionTranslationKey());
    }

    public void write(PacketByteBuf buffer) {
        SerializableData.Instance data = DATA.new Instance();
        data.set("icon", itemIcon);
        data.set("action_on_chosen", action);
        data.set("powers", powerTypes.stream().map(PowerType::getIdentifier).collect(Collectors.toList()));
        data.set("name", getOrCreateNameTranslationKey());
        data.set("description", getOrCreateDescriptionTranslationKey());
        DATA.write(buffer, data);
    }

    @SuppressWarnings("unchecked")
    public static Choice createFromData(Identifier id, SerializableData.Instance data) {
        Choice choice = new Choice(id);

        ((List<Identifier>)data.get("powers")).forEach(powerId -> {
            try {
                PowerType<?> powerType = PowerTypeRegistry.get(powerId);
                choice.add(powerType);
            } catch(IllegalArgumentException e) {
                CCPacksApoli.LOGGER.error("Choice \"" + id + "\" contained unregistered power: \"" + powerId + "\"");
            }
        });
        choice.setIcon(data.get("icon"));
        choice.setAction((ActionFactory<Entity>.Instance)data.get("action_on_chosen"));
        choice.setName(data.getString("name"));
        choice.setDescription(data.getString("description"));

        return choice;
    }


    public static Choice fromJson(Identifier id, JsonObject json) {
        return createFromData(id, DATA.read(json));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Choice(" + identifier.toString() + ")[");
        for(PowerType<?> pt : powerTypes) {
            str.append(PowerTypeRegistry.getId(pt));
            str.append(",");
        }
        str = new StringBuilder(str.substring(0, str.length() - 1) + "]");
        return str.toString();
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Choice) {
            return ((Choice)obj).identifier.equals(identifier);
        }
        return false;
    }
}
