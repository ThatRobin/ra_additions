package io.github.thatrobin.ra_additions_tags.registries;

import io.github.thatrobin.ra_additions_tags.data_loaders.ActionType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BlockActionRegistry {
    private static final HashMap<Identifier, ActionType> idToBlockAction = new HashMap<>();

    public static ActionType register(Identifier id, ActionType powerType) {
        if(idToBlockAction.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate block action type id tried to register: '" + id.toString() + "'");
        }
        idToBlockAction.put(id, powerType);
        return powerType;
    }

    public static ActionType update(Identifier id, ActionType powerType) {
        idToBlockAction.remove(id);
        return register(id, powerType);
    }

    public static int size() {
        return idToBlockAction.size();
    }

    public static Stream<Identifier> identifiers() {
        return idToBlockAction.keySet().stream();
    }

    @SuppressWarnings("unused")
    public static Iterable<Map.Entry<Identifier, ActionType>> entries() {
        return idToBlockAction.entrySet();
    }

    public static Iterable<ActionType> values() {
        return idToBlockAction.values();
    }

    public static ActionType get(Identifier id) {
        if(!idToBlockAction.containsKey(id)) {
            throw new IllegalArgumentException("Could not get block action type from id '" + id.toString() + "', as it was not registered!");
        }
        return idToBlockAction.get(id);
    }

    @SuppressWarnings("unused")
    public static Identifier getId(ActionType powerType) {
        return powerType.getIdentifier();
    }

    public static boolean contains(Identifier id) {
        return idToBlockAction.containsKey(id);
    }

    public static void clear() {
        idToBlockAction.clear();
    }

    public static void reset() {
        clear();
    }
}
