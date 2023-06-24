package io.github.thatrobin.ra_additions.mechanics;

import io.github.thatrobin.ra_additions.component.ClaimedLand;
import net.minecraft.util.Identifier;

public class MechanicType<T extends Mechanic> {

    private final Identifier identifier;
    private final MechanicFactory<? extends Mechanic>.Instance factory;

    public MechanicType(Identifier id, MechanicFactory<? extends Mechanic>.Instance factory) {
        this.identifier = id;
        this.factory = factory;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public MechanicFactory<? extends Mechanic>.Instance getFactory() {
        return factory;
    }

    public T create(ClaimedLand land) {
        return (T) getFactory().apply(this, land);
    }

    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof MechanicType)) {
            return false;
        }
        Identifier id = ((MechanicType<?>)obj).getIdentifier();
        return identifier.equals(id);
    }
}
