package io.github.thatrobin.ra_additions_tags.registries;

import io.github.thatrobin.ra_additions_tags.data_loaders.ConditionType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ItemConditionRegistry {
    private static final HashMap<Identifier, ConditionType> idToItemCondition = new HashMap<>();

    public static ConditionType register(Identifier id, ConditionType powerType) {
        if(idToItemCondition.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate item condition type id tried to register: '" + id.toString() + "'");
        }
        idToItemCondition.put(id, powerType);
        return powerType;
    }

    public static ConditionType update(Identifier id, ConditionType powerType) {
        idToItemCondition.remove(id);
        return register(id, powerType);
    }

    public static int size() {
        return idToItemCondition.size();
    }

    @SuppressWarnings("unused")
    public static Stream<Identifier> identifiers() {
        return idToItemCondition.keySet().stream();
    }

    @SuppressWarnings("unused")
    public static Iterable<Map.Entry<Identifier, ConditionType>> entries() {
        return idToItemCondition.entrySet();
    }

    public static Iterable<ConditionType> values() {
        return idToItemCondition.values();
    }

    public static ConditionType get(Identifier id) {
        if(!idToItemCondition.containsKey(id)) {
            throw new IllegalArgumentException("Could not get item condition type from id '" + id.toString() + "', as it was not registered!");
        }
        return idToItemCondition.get(id);
    }

    @SuppressWarnings("unused")
    public static Identifier getId(ConditionType powerType) {
        return powerType.getIdentifier();
    }

    public static boolean contains(Identifier id) {
        return idToItemCondition.containsKey(id);
    }

    public static void clear() {
        idToItemCondition.clear();
    }

    public static void reset() {
        clear();
    }
}
