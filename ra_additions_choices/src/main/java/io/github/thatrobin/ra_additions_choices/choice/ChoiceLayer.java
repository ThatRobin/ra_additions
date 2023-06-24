package io.github.thatrobin.ra_additions_choices.choice;

import com.google.common.collect.Lists;
import com.google.gson.*;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionTypes;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChoiceLayer implements Comparable<ChoiceLayer> {

    private Identifier identifier;
    private List<ConditionedChoice> choices;
    private boolean enabled = false;

    private String nameTranslationKey;

    public String getOrCreateTranslationKey() {
        if(nameTranslationKey == null || nameTranslationKey.isEmpty()) {
            this.nameTranslationKey = "layer." + identifier.getNamespace() + "." + identifier.getPath() + ".name";
        }
        return nameTranslationKey;
    }

    public String getTranslationKey() {
        return getOrCreateTranslationKey();
    }

    public Identifier getIdentifier() {
        return identifier;
    }



    public boolean isEnabled() {
        return enabled;
    }

    @SuppressWarnings("unused")
    public List<Identifier> getChoices() {
        return choices.stream().flatMap(co -> co.getChoices().stream()).filter(ChoiceRegistry::contains).collect(Collectors.toList());
    }

    public List<Identifier> getChoices(PlayerEntity playerEntity) {
        return choices.stream().filter(co -> co.isConditionFulfilled(playerEntity)).flatMap(co -> co.getChoices().stream()).filter(ChoiceRegistry::contains).collect(Collectors.toList());
    }

    public int getChoiceOptionCount(PlayerEntity player) {
        long choosableChoices = getChoices(player).stream().map(ChoiceRegistry::get).count();
        return (int)choosableChoices;
    }

    public boolean contains(Choice choice) {
        return choices.stream().anyMatch(co -> co.getChoices().stream().anyMatch(o -> o.equals(choice.getIdentifier())));
    }

    public void merge(JsonObject json) {
        if(json.has("enabled")) {
            this.enabled = json.get("enabled").getAsBoolean();
        }
        if(json.has("choices")) {
            JsonArray originArray = json.getAsJsonArray("choices");
            originArray.forEach(je -> this.choices.add(ConditionedChoice.read(je)));
        }
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        } else if(!(obj instanceof ChoiceLayer)) {
            return false;
        } else {
            return identifier.equals(((ChoiceLayer)obj).identifier);
        }
    }

    public void write(PacketByteBuf buffer) {
        buffer.writeString(identifier.toString());
        buffer.writeBoolean(enabled);
        buffer.writeInt(choices.size());
        choices.forEach(co -> co.write(buffer));
        buffer.writeString(getOrCreateTranslationKey());
    }

    @Environment(EnvType.CLIENT)
    public static ChoiceLayer read(PacketByteBuf buffer) {
        ChoiceLayer layer = new ChoiceLayer();
        layer.identifier = Identifier.tryParse(buffer.readString());
        layer.enabled = buffer.readBoolean();
        int conditionedOriginCount = buffer.readInt();
        layer.choices = new ArrayList<>(conditionedOriginCount);
        for(int i = 0; i < conditionedOriginCount; i++) {
            layer.choices.add(ConditionedChoice.read(buffer));
        }
        layer.nameTranslationKey = buffer.readString();
        return layer;
    }

    public static ChoiceLayer createFromData(Identifier id, JsonObject json) {
        JsonArray choiceArray = json.getAsJsonArray("choices");
        List<ConditionedChoice> list = new ArrayList<>(choiceArray.size());
        choiceArray.forEach(je -> list.add(ConditionedChoice.read(je)));
        boolean enabled = JsonHelper.getBoolean(json, "enabled", true);
        ChoiceLayer choiceLayer = new ChoiceLayer();
        choiceLayer.identifier = id;
        choiceLayer.choices = list;
        choiceLayer.enabled = enabled;
        return choiceLayer;
    }

    @Override
    public int compareTo(@NotNull ChoiceLayer o) {
        return 0;
    }

    public record ConditionedChoice(
            ConditionFactory<Entity>.Instance condition,
            List<Identifier> choices) {

        public boolean isConditionFulfilled(PlayerEntity playerEntity) {
            return condition == null || condition.test(playerEntity);
        }

        public List<Identifier> getChoices() {
            return choices;
        }

        private static final SerializableData conditionedOriginObjectData = new SerializableData()
                .add("condition", ApoliDataTypes.ENTITY_CONDITION)
                .add("choices", SerializableDataTypes.IDENTIFIERS);

        public void write(PacketByteBuf buffer) {
            buffer.writeBoolean(condition != null);
            if (condition != null)
                condition.write(buffer);
            buffer.writeInt(choices.size());
            choices.forEach(buffer::writeIdentifier);
        }

        @Environment(EnvType.CLIENT)
        public static ConditionedChoice read(PacketByteBuf buffer) {
            ConditionFactory<Entity>.Instance condition = null;
            if (buffer.readBoolean()) {
                condition = ConditionTypes.ENTITY.read(buffer);
            }
            int originCount = buffer.readInt();
            List<Identifier> originList = new ArrayList<>(originCount);
            for (int i = 0; i < originCount; i++) {
                originList.add(buffer.readIdentifier());
            }
            return new ConditionedChoice(condition, originList);
        }

        public static ConditionedChoice read(JsonElement element) {
            if (element.isJsonPrimitive()) {
                JsonPrimitive elemPrimitive = element.getAsJsonPrimitive();
                if (elemPrimitive.isString()) {
                    return new ConditionedChoice(null, Lists.newArrayList(Identifier.tryParse(elemPrimitive.getAsString())));
                }
                throw new JsonParseException("Expected choice in layer to be either a string or an object.");
            } else if (element.isJsonObject()) {
                SerializableData.Instance data = conditionedOriginObjectData.read(element.getAsJsonObject());
                return new ConditionedChoice(data.get("condition"), data.get("choices"));
            }
            throw new JsonParseException("Expected choice in layer to be either a string or an object.");
        }
    }
}