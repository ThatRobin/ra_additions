package io.github.thatrobin.ra_additions.goals.factories;

import io.github.apace100.apoli.power.PowerTypeReference;
import net.minecraft.util.Identifier;

public class GoalTypeReference<T extends Goal> extends GoalType<T> {

    private GoalType<T> referencedPowerType;

    public GoalTypeReference(Identifier id) {
        super(id, null);
    }

    @Override
    public GoalFactory<T>.Instance getFactory() {
        getReferencedPowerType();
        if(referencedPowerType == null) {
            return null;
        }
        return referencedPowerType.getFactory();
    }

    @SuppressWarnings("all")
    public GoalType<T> getReferencedPowerType() {
        if(isReferenceInvalid()) {
            try {
                referencedPowerType = null;
                referencedPowerType = GoalRegistry.get(getIdentifier());
            } catch(IllegalArgumentException e) {
            }
        }
        return referencedPowerType;
    }

    @SuppressWarnings("rawtypes")
    private boolean isReferenceInvalid() {
        if(referencedPowerType != null) {
            if(GoalRegistry.contains(referencedPowerType.getIdentifier())) {
                GoalType type = GoalRegistry.get(referencedPowerType.getIdentifier());
                return type != referencedPowerType;
            }
        }
        return true;
    }
}
