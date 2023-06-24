package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.data.RAA_DataTypes;
import io.github.thatrobin.ra_additions.util.BossBarHudRender;
import io.github.thatrobin.ra_additions.util.BossBarHudRendered;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class BossBarPower extends VariableIntPower implements BossBarHudRendered {

    private final Consumer<Entity> actionOnMin;
    private final Consumer<Entity> actionOnMax;
    private final BossBarHudRender hudRender;
    private final Text text;

    public BossBarPower(PowerType<?> type, LivingEntity entity, int min, int max, BossBarHudRender hudRender, int startValue, Consumer<Entity> actionOnMin, Consumer<Entity> actionOnMax, Text text) {
        super(type, entity, startValue, min, max);
        this.actionOnMin = actionOnMin;
        this.text = text;
        this.actionOnMax = actionOnMax;
        this.hudRender = hudRender;
    }

    @Override
    public int setValue(int newValue) {
        int oldValue = currentValue;
        int actualNewValue = super.setValue(newValue);
        if(oldValue != actualNewValue) {
            if(actionOnMin != null && actualNewValue == min) {
                actionOnMin.accept(entity);
            }
            if(actionOnMax != null && actualNewValue == max) {
                actionOnMax.accept(entity);
            }
        }
        return actualNewValue;
    }

    @Override
    public BossBarHudRender getRenderSettings() {
        return this.hudRender;
    }

    @Override
    public float getFill() {
        return this.currentValue;
    }

    public float getPercentage() {
        float total = this.max - this.min;
        return this.currentValue / total;
    }

    @Override
    public boolean shouldRender() {
        if(entity instanceof PlayerEntity player) {
            return getRenderSettings().shouldRender(player);
        } else {
            return false;
        }
    }

    public Text getText() {
        return text;
    }

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("boss_bar"),
                new SerializableDataExt()
                        .add("min", "The minimum value of the boss bar.", SerializableDataTypes.INT)
                        .add("max", "The maximum value of the boss bar.", SerializableDataTypes.INT)
                        .addFunctionedDefault("start_value", "The value of the boss bar when the entity first receives the power. If not set, this will be set to the value of the min integer field.", SerializableDataTypes.INT, data -> data.getInt("min"))
                        .add("hud_render", "Determines how the boss bar is visualized on the HUD.", RAA_DataTypes.BOSS_BAR_HUD_RENDER)
                        .add("text", "The text displayed above the boss bar.", SerializableDataTypes.TEXT, null)
                        .add("min_action", "If specified, this action will be executed on the entity whenever the minimum value is reached.", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("max_action", "If specified, this action will be executed on the entity whenever the maximum value is reached.", ApoliDataTypes.ENTITY_ACTION, null),
                data ->
                        (type, entity) -> new BossBarPower(type, entity, data.getInt("min"), data.getInt("max"), data.get("hud_render"), data.getInt("start_value"), data.get("min_action"), data.get("max_action"), data.get("text")))
                .allowCondition();
    }
}
