package io.github.thatrobin.ccpacksapoli.power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;

import java.util.function.Consumer;

public class CustomToggle extends Power implements Active {

    private final Consumer<Entity> entityAction;
    private final Consumer<Entity> entityAction2;
    private boolean isActive;
    private final boolean shouldRetainState;

    public CustomToggle(PowerType<?> type, LivingEntity entity, Consumer<Entity> entityAction, Consumer<Entity> entityAction2, boolean activeByDefault, boolean shouldRetainState) {
        super(type, entity);
        this.entityAction = entityAction;
        this.entityAction2 = entityAction2;
        this.shouldRetainState = shouldRetainState;
        this.isActive = activeByDefault;
    }

    @Override
    public boolean shouldTick() {
        return !shouldRetainState && this.conditions.size() > 0;
    }

    @Override
    public boolean shouldTickWhenInactive() {
        return true;
    }

    @Override
    public void tick() {
        if(!super.isActive() && this.isActive) {
            this.isActive = false;
            executeActions();
            PowerHolderComponent.syncPower(entity, this.type);
        }
    }

    @Override
    public void onUse() {
        this.isActive = !this.isActive;
        if(super.isActive()) {
            executeActions();
        }
        PowerHolderComponent.syncPower(entity, this.type);
    }

    public boolean isActive() {
        return this.isActive && super.isActive();
    }

    @Override
    public NbtElement toTag() {
        return NbtByte.of(isActive);
    }

    @Override
    public void fromTag(NbtElement tag) {
        isActive = ((NbtByte)tag).byteValue() > 0;
    }

    private Key key;

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }

    public void executeActions() {
        if (isActive) {
            if (entityAction != null) {
                entityAction.accept(entity);
            }
        } else {
            if (entityAction2 != null) {
                entityAction2.accept(entity);
            }
        }
    }
}
