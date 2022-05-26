package io.github.thatrobin.ccpacksapoli.choice;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ChoiceRegistry {
    private static final HashMap<Identifier, Choice> idToChoice = new HashMap<>();

    public static Choice register(Choice choice) {
        return register(choice.getIdentifier(), choice);
    }

    public static Choice register(Identifier id, Choice choice) {
        if(idToChoice.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate choice id tried to register: '" + id.toString() + "'");
        }
        idToChoice.put(id, choice);
        return choice;
    }

    public static int size() {
        return idToChoice.size();
    }

    public static Choice get(Identifier id) {
        if(!idToChoice.containsKey(id)) {
            throw new IllegalArgumentException("Could not get choice from id '" + id.toString() + "', as it was not registered!");
        }
        Choice choice = idToChoice.get(id);
        return choice;
    }

    public static boolean contains(Identifier id) {
        return idToChoice.containsKey(id);
    }

    public static boolean contains(Choice choice) {
        return contains(choice.getIdentifier());
    }

    public static void clear() {
        idToChoice.clear();
    }

    public static Iterable<Map.Entry<Identifier, Choice>> entries() {
        return idToChoice.entrySet();
    }

    public static void reset() {
        clear();
        register(Choice.EMPTY);
    }
}
