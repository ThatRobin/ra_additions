package io.github.thatrobin.ra_additions_tags.registries;

import io.github.thatrobin.ra_additions_tags.data_loaders.ActionType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class EntityActionRegistry {
    private static final HashMap<Identifier, ActionType> idToEntityAction = new HashMap<>();

    public static ActionType register(Identifier id, ActionType powerType) {
        if(idToEntityAction.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate entity action type id tried to register: '" + id.toString() + "'");
        }
        idToEntityAction.put(id, powerType);
        return powerType;
    }

    public static ActionType update(Identifier id, ActionType powerType) {
        idToEntityAction.remove(id);
        return register(id, powerType);
    }

    public static int size() {
        return idToEntityAction.size();
    }

    public static Stream<Identifier> identifiers() {
        return idToEntityAction.keySet().stream();
    }

    @SuppressWarnings("unused")
    public static Iterable<Map.Entry<Identifier, ActionType>> entries() {
        return idToEntityAction.entrySet();
    }

    public static Iterable<ActionType> values() {
        return idToEntityAction.values();
    }

    public static ActionType get(Identifier id) {
        if(!idToEntityAction.containsKey(id)) {
            throw new IllegalArgumentException("Could not get entity action type from id '" + id.toString() + "', as it was not registered!");
        }
        return idToEntityAction.get(id);
    }

    @SuppressWarnings("unused")
    public static Identifier getId(ActionType powerType) {
        return powerType.getIdentifier();
    }

    public static boolean contains(Identifier id) {
        return idToEntityAction.containsKey(id);
    }

    public static void clear() {
        idToEntityAction.clear();
    }

    public static void reset() {
        clear();
    }
}
