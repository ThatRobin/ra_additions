package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.data.RAA_DataTypes;
import io.github.thatrobin.ra_additions.util.StatBarHudRender;
import net.minecraft.entity.LivingEntity;

public class StatBarPower extends VariableIntPower {

    private final StatBarHudRender hudRender;

    public StatBarPower(PowerType<?> type, LivingEntity entity, StatBarHudRender hudRender) {
        super(type, entity,20,0,20);
        this.hudRender = hudRender;
    }

    public StatBarHudRender getHudRender() {
        return this.hudRender;
    }

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("stat_bar"),
                new SerializableData()
                        .add("start_value", SerializableDataTypes.INT, 20)
                        .add("hud_render", RAA_DataTypes.STAT_BAR_HUD_RENDER),
                data ->
                        (type, entity) -> new StatBarPower(type, entity, data.get("hud_render")))
                .allowCondition();
    }

}
