package io.github.thatrobin.ra_additions_goals.factories.goals.utils;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("UnusedReturnValue")
public class GoalRegistry {
    private static final HashMap<Identifier, GoalType<?>> idToContent = new HashMap<>();

    public static GoalType<?> register(Identifier id, GoalType<?> contentType) {
        if(idToContent.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate goal type id tried to register: '" + id.toString() + "'");
        }
        idToContent.put(id, contentType);
        return contentType;
    }

    public static int size() {
        return idToContent.size();
    }

    @SuppressWarnings("unused")
    public static Stream<Identifier> identifiers() {
        return idToContent.keySet().stream();
    }

    @SuppressWarnings("unused")
    public static Iterable<Map.Entry<Identifier, GoalType<?>>> entries() {
        return idToContent.entrySet();
    }

    @SuppressWarnings("rawtypes")
    public static GoalType get(Identifier id) {
        if(!idToContent.containsKey(id)) {
            throw new IllegalArgumentException("Could not get goal type from id '" + id.toString() + "', as it was not registered!");
        }
        return idToContent.get(id);
    }

    @SuppressWarnings("unused")
    public static Identifier getId(GoalType<?> powerType) {
        return powerType.getIdentifier();
    }

    public static boolean contains(Identifier id) {
        return idToContent.containsKey(id);
    }

    public static void clear() {
        idToContent.clear();
    }

    @SuppressWarnings("unused")
    public static void reset() {
        clear();
    }
}
