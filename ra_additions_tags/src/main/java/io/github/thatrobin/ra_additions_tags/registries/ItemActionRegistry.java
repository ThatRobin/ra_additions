package io.github.thatrobin.ra_additions_tags.registries;

import io.github.thatrobin.ra_additions_tags.data_loaders.ActionType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ItemActionRegistry {
    private static final HashMap<Identifier, ActionType> idToItemAction = new HashMap<>();

    public static ActionType register(Identifier id, ActionType powerType) {
        if(idToItemAction.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate item action type id tried to register: '" + id.toString() + "'");
        }
        idToItemAction.put(id, powerType);
        return powerType;
    }

    public static ActionType update(Identifier id, ActionType powerType) {
        idToItemAction.remove(id);
        return register(id, powerType);
    }

    public static int size() {
        return idToItemAction.size();
    }

    public static Stream<Identifier> identifiers() {
        return idToItemAction.keySet().stream();
    }

    @SuppressWarnings("unused")
    public static Iterable<Map.Entry<Identifier, ActionType>> entries() {
        return idToItemAction.entrySet();
    }

    public static Iterable<ActionType> values() {
        return idToItemAction.values();
    }

    public static ActionType get(Identifier id) {
        if(!idToItemAction.containsKey(id)) {
            throw new IllegalArgumentException("Could not get item action type from id '" + id.toString() + "', as it was not registered!");
        }
        return idToItemAction.get(id);
    }

    @SuppressWarnings("unused")
    public static Identifier getId(ActionType powerType) {
        return powerType.getIdentifier();
    }

    public static boolean contains(Identifier id) {
        return idToItemAction.containsKey(id);
    }

    public static void clear() {
        idToItemAction.clear();
    }

    public static void reset() {
        clear();
    }
}
