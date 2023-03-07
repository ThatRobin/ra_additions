package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;

import java.util.function.Consumer;

public class CustomTogglePower extends Power implements Active {

    private final Consumer<Entity> entityAction;
    private final Consumer<Entity> entityAction2;
    private boolean isActive;
    private final boolean shouldRetainState;

    public CustomTogglePower(PowerType<?> type, LivingEntity entity, Consumer<Entity> entityAction, Consumer<Entity> entityAction2, boolean activeByDefault, boolean shouldRetainState) {
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

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory(String label) {
        return new PowerFactory<>(RA_Additions.identifier("toggle"),
                new SerializableDataExt(label)
                        .add("active_by_default", "Whether this power starts in the on or off state.", SerializableDataTypes.BOOLEAN, true)
                        .add("retain_state", "Whether this power switches back to default if the condition is no longer met.", SerializableDataTypes.BOOLEAN, true)
                        .add("toggle_on_action", "The entity action to be executed when the power is toggled on.", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("toggle_off_action", "The entity action to be executed when the power is toggled off.", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("key", "Which active key this power should respond to.", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key()),
                data ->
                        (type, entity) -> {
                            CustomTogglePower toggle = new CustomTogglePower(type, entity, data.get("toggle_on_action"), data.get("toggle_off_action"), data.getBoolean("active_by_default"), data.getBoolean("retain_state"));
                            toggle.setKey(data.get("key"));
                            return toggle;
                        }).allowCondition();
    }
}
