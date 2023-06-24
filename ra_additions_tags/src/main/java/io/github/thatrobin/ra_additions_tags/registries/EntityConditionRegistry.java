package io.github.thatrobin.ra_additions_tags.registries;

import io.github.thatrobin.ra_additions_tags.data_loaders.ConditionType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class EntityConditionRegistry {
    private static final HashMap<Identifier, ConditionType> idToEntityCondition = new HashMap<>();

    public static ConditionType register(Identifier id, ConditionType powerType) {
        if(idToEntityCondition.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate entity condition type id tried to register: '" + id.toString() + "'");
        }
        idToEntityCondition.put(id, powerType);
        return powerType;
    }

    public static ConditionType update(Identifier id, ConditionType powerType) {
        idToEntityCondition.remove(id);
        return register(id, powerType);
    }

    public static int size() {
        return idToEntityCondition.size();
    }

    @SuppressWarnings("unused")
    public static Stream<Identifier> identifiers() {
        return idToEntityCondition.keySet().stream();
    }

    @SuppressWarnings("unused")
    public static Iterable<Map.Entry<Identifier, ConditionType>> entries() {
        return idToEntityCondition.entrySet();
    }

    public static Iterable<ConditionType> values() {
        return idToEntityCondition.values();
    }

    public static ConditionType get(Identifier id) {
        if(!idToEntityCondition.containsKey(id)) {
            throw new IllegalArgumentException("Could not get entity condition type from id '" + id.toString() + "', as it was not registered!");
        }
        return idToEntityCondition.get(id);
    }

    @SuppressWarnings("unused")
    public static Identifier getId(ConditionType powerType) {
        return powerType.getIdentifier();
    }

    public static boolean contains(Identifier id) {
        return idToEntityCondition.containsKey(id);
    }

    public static void clear() {
        idToEntityCondition.clear();
    }

    public static void reset() {
        clear();
    }
}
