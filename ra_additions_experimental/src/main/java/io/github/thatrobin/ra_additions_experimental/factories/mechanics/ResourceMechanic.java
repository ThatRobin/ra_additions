package io.github.thatrobin.ra_additions_experimental.factories.mechanics;

import io.github.thatrobin.ra_additions_experimental.component.ClaimedLand;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;

public class ResourceMechanic extends Mechanic {

    protected final int min, max;
    protected int currentValue;
    private final String name;

    public ResourceMechanic(MechanicType<?> mechanicType, ClaimedLand claimedLand, int startValue, int min, int max) {
        super(mechanicType, claimedLand);
        this.name = mechanicType.getIdentifier().getPath();
        this.currentValue = startValue;
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getValue() {
        return currentValue;
    }

    public String getName() {
        return name;
    }

    public int setValue(int newValue) {
        if(newValue > getMax())
            newValue = getMax();
        if(newValue < getMin())
            newValue = getMin();
        return currentValue = newValue;
    }

    @SuppressWarnings("unused")
    public int increment() {
        return setValue(getValue() + 1);
    }

    @SuppressWarnings("unused")
    public int decrement() {
        return setValue(getValue() - 1);
    }

    @Override
    public NbtElement toTag() {
        return NbtInt.of(currentValue);
    }

    @Override
    public void fromTag(NbtElement tag) {
        currentValue = ((NbtInt)tag).intValue();
    }

}
