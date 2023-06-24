package io.github.thatrobin.ra_additions_choices.choice;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.MultiplePowerType;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_choices.component.ModComponents;
import io.github.thatrobin.ra_additions.data.RAA_DataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Choice {

    public static final SerializableData DATA = new SerializableData()
            .add("powers", SerializableDataTypes.IDENTIFIERS, Lists.newArrayList())
            .add("name", SerializableDataTypes.STRING, "")
            .add("icon", RAA_DataTypes.ITEM_OR_ITEM_STACK, new ItemStack(Items.AIR))
            .add("action_on_chosen", ApoliDataTypes.ENTITY_ACTION, null)
            .add("description", SerializableDataTypes.STRING, "");

    public static final Choice EMPTY;

    static {
        EMPTY = register(new Choice(RA_Additions.identifier("empty")).setSpecial());
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

    private boolean isSpecial;

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

    public Choice setSpecial() {
        this.isSpecial = true;
        return this;
    }

    @SuppressWarnings("all")
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

    public boolean isSpecial() {
        return this.isSpecial;
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

    public MutableText getName() {
        return Text.translatable(getOrCreateNameTranslationKey());
    }

    public String getOrCreateDescriptionTranslationKey() {
        if(descriptionTranslationKey == null || descriptionTranslationKey.isEmpty()) {
            descriptionTranslationKey =
                    "choice." + identifier.getNamespace() + "." + identifier.getPath() + ".description";
        }
        return descriptionTranslationKey;
    }

    public MutableText getDescription() {
        return Text.translatable(getOrCreateDescriptionTranslationKey());
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
                RA_Additions.LOGGER.error("Choice \"" + id + "\" contained unregistered power: \"" + powerId + "\"");
            }
        });
        /*
        Collection<PowerType<?>> tag = PowerTagManager.POWER_TAG_LOADER.getTagOrEmpty(id);
        if(tag.size() > 0) {
            for (PowerType<?> powerType : tag) {
                choice.add(powerType);
            }
        }
         */
        choice.setIcon(data.get("icon"));
        choice.setAction(data.get("action_on_chosen"));
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
