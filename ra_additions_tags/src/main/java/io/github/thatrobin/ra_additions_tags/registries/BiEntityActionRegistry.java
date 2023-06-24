package io.github.thatrobin.ra_additions_tags.registries;

import io.github.thatrobin.ra_additions_tags.data_loaders.ActionType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BiEntityActionRegistry {
    private static final HashMap<Identifier, ActionType> idToBiEntityAction = new HashMap<>();

    public static ActionType register(Identifier id, ActionType powerType) {
        if(idToBiEntityAction.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate bientity action type id tried to register: '" + id.toString() + "'");
        }
        idToBiEntityAction.put(id, powerType);
        return powerType;
    }

    public static ActionType update(Identifier id, ActionType powerType) {
        idToBiEntityAction.remove(id);
        return register(id, powerType);
    }

    public static int size() {
        return idToBiEntityAction.size();
    }

    public static Stream<Identifier> identifiers() {
        return idToBiEntityAction.keySet().stream();
    }

    @SuppressWarnings("unused")
    public static Iterable<Map.Entry<Identifier, ActionType>> entries() {
        return idToBiEntityAction.entrySet();
    }

    public static Iterable<ActionType> values() {
        return idToBiEntityAction.values();
    }

    public static ActionType get(Identifier id) {
        if(!idToBiEntityAction.containsKey(id)) {
            throw new IllegalArgumentException("Could not get bientity action type from id '" + id.toString() + "', as it was not registered!");
        }
        return idToBiEntityAction.get(id);
    }

    @SuppressWarnings("unused")
    public static Identifier getId(ActionType powerType) {
        return powerType.getIdentifier();
    }

    public static boolean contains(Identifier id) {
        return idToBiEntityAction.containsKey(id);
    }

    public static void clear() {
        idToBiEntityAction.clear();
    }

    public static void reset() {
        clear();
    }
}
