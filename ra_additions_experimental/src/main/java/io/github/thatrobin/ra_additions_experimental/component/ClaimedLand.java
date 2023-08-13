package io.github.thatrobin.ra_additions_experimental.component;

import io.github.thatrobin.ra_additions_experimental.factories.mechanics.Mechanic;
import io.github.thatrobin.ra_additions_experimental.factories.mechanics.MechanicType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClaimedLand implements MechanicHolder {

    private final HashMap<MechanicType<?>, Mechanic> powers = new HashMap<>();

    @Override
    public void removePower(MechanicType<?> powerType) {
        powers.remove(powerType);
    }

    @Override
    public boolean addPower(MechanicType<?> mechanicType) {
        powers.put(mechanicType, mechanicType.create(this));
        return powers.containsKey(mechanicType);
    }

    @Override
    public boolean hasPower(MechanicType<?> powerType) {
        return powers.containsKey(powerType);
    }

    @Override
    public <T extends Mechanic> T getPower(MechanicType<T> powerType) {
        return (T)powers.get(powerType);
    }

    @Override
    public List<Mechanic> getPowers() {
        return powers.values().stream().toList();
    }

    public Set<Map.Entry<MechanicType<?>, Mechanic>> entrySet() {
        return powers.entrySet();
    }

    @SuppressWarnings("unused")
    public List<MechanicType<?>> keys() {
        return powers.keySet().stream().toList();
    }

    @Override
    public <T extends Mechanic> List<Mechanic> getPowers(Class<T> powerClass) {
        return powers.values().stream().filter((powerClass::isInstance)).toList();
    }
}
