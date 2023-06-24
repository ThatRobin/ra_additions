package io.github.thatrobin.ra_additions_tags.registries;

import io.github.thatrobin.ra_additions_tags.data_loaders.ConditionType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BlockConditionRegistry {
    private static final HashMap<Identifier, ConditionType> idToBlockCondition = new HashMap<>();

    public static ConditionType register(Identifier id, ConditionType powerType) {
        if(idToBlockCondition.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate block condition type id tried to register: '" + id.toString() + "'");
        }
        idToBlockCondition.put(id, powerType);
        return powerType;
    }

    public static ConditionType update(Identifier id, ConditionType powerType) {
        idToBlockCondition.remove(id);
        return register(id, powerType);
    }

    public static int size() {
        return idToBlockCondition.size();
    }

    @SuppressWarnings("unused")
    public static Stream<Identifier> identifiers() {
        return idToBlockCondition.keySet().stream();
    }

    @SuppressWarnings("unused")
    public static Iterable<Map.Entry<Identifier, ConditionType>> entries() {
        return idToBlockCondition.entrySet();
    }

    public static Iterable<ConditionType> values() {
        return idToBlockCondition.values();
    }

    public static ConditionType get(Identifier id) {
        if(!idToBlockCondition.containsKey(id)) {
            throw new IllegalArgumentException("Could not get block condition type from id '" + id.toString() + "', as it was not registered!");
        }
        return idToBlockCondition.get(id);
    }

    @SuppressWarnings("unused")
    public static Identifier getId(ConditionType powerType) {
        return powerType.getIdentifier();
    }

    public static boolean contains(Identifier id) {
        return idToBlockCondition.containsKey(id);
    }

    public static void clear() {
        idToBlockCondition.clear();
    }

    public static void reset() {
        clear();
    }
}
