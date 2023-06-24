package io.github.thatrobin.ra_additions_tags.registries;

import io.github.thatrobin.ra_additions_tags.data_loaders.ConditionType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BiEntityConditionRegistry {
    private static final HashMap<Identifier, ConditionType> idToBiEntityCondition = new HashMap<>();

    public static ConditionType register(Identifier id, ConditionType powerType) {
        if(idToBiEntityCondition.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate bientity condition type id tried to register: '" + id.toString() + "'");
        }
        idToBiEntityCondition.put(id, powerType);
        return powerType;
    }

    public static ConditionType update(Identifier id, ConditionType powerType) {
        idToBiEntityCondition.remove(id);
        return register(id, powerType);
    }

    public static int size() {
        return idToBiEntityCondition.size();
    }

    @SuppressWarnings("unused")
    public static Stream<Identifier> identifiers() {
        return idToBiEntityCondition.keySet().stream();
    }

    @SuppressWarnings("unused")
    public static Iterable<Map.Entry<Identifier, ConditionType>> entries() {
        return idToBiEntityCondition.entrySet();
    }

    public static Iterable<ConditionType> values() {
        return idToBiEntityCondition.values();
    }

    public static ConditionType get(Identifier id) {
        if(!idToBiEntityCondition.containsKey(id)) {
            throw new IllegalArgumentException("Could not get bientity condition type from id '" + id.toString() + "', as it was not registered!");
        }
        return idToBiEntityCondition.get(id);
    }

    @SuppressWarnings("unused")
    public static Identifier getId(ConditionType powerType) {
        return powerType.getIdentifier();
    }

    public static boolean contains(Identifier id) {
        return idToBiEntityCondition.containsKey(id);
    }

    public static void clear() {
        idToBiEntityCondition.clear();
    }

    public static void reset() {
        clear();
    }
}
