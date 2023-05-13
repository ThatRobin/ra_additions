package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

public class ValuePower extends VariableIntPower {

    private String valueTranslationKey;
    private final int posX;
    private final int posY;

    public ValuePower(PowerType<?> type, LivingEntity entity, int startValue, int min, int max, int posX, int posY) {
        super(type, entity, startValue, min, max);
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Text getOrCreateValueTranslationKey() {
        if(valueTranslationKey == null || valueTranslationKey.isEmpty()) {
            valueTranslationKey =
                    "power." + type.getIdentifier().getNamespace() + "." + type.getIdentifier().getPath() + ".value";
        }
        return Text.translatable(valueTranslationKey, this.getValue());
    }

    private void setValueTranslationKey(String valueTranslationKey) {
        this.valueTranslationKey = valueTranslationKey;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("value"),
                new SerializableDataExt()
                        .add("min", "The minimum value of the power.", SerializableDataTypes.INT, Integer.MIN_VALUE)
                        .add("max", "The maximum value of the power.", SerializableDataTypes.INT,Integer.MAX_VALUE)
                        .add("x", "The X co-ordinate that the string will appear at.", SerializableDataTypes.INT, 0)
                        .add("y", "The Y co-ordinate that the string will appear at.", SerializableDataTypes.INT, 0)
                        .add("value_key", "The string that will contain the value", SerializableDataTypes.STRING)
                        .addFunctionedDefault("start_value", "The value of the power when the entity first receives it. If not set, this will be set to the value of the min integer field.", SerializableDataTypes.INT, data -> data.getInt("min")),
                data ->
                        (type, player) -> {
                            ValuePower valuePower = new ValuePower(type, player,
                                        data.getInt("start_value"),
                                        data.getInt("min"),
                                        data.getInt("max"),
                                        data.getInt("x"),
                                        data.getInt("y"));
                            if(data.isPresent("value_key")) {
                                valuePower.setValueTranslationKey(data.getString("value_key"));
                            }
                            return valuePower;
                        })

                .allowCondition();
    }
}
