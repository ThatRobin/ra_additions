package io.github.thatrobin.ra_additions.component;

import io.github.thatrobin.ra_additions.mechanics.Mechanic;
import io.github.thatrobin.ra_additions.mechanics.MechanicType;

import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface MechanicHolder {

    void removePower(MechanicType<?> powerType);

    boolean addPower(MechanicType<?> powerType);

    boolean hasPower(MechanicType<?> powerType);


    <T extends Mechanic> T getPower(MechanicType<T> powerType);

    List<Mechanic> getPowers();

    <T extends Mechanic> List<Mechanic> getPowers(Class<T> powerClass);

}
