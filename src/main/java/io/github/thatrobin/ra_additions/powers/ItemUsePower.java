package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemUsePower extends Power {

    private final Predicate<ItemStack> itemCondition;
    private final Consumer<Entity> entityAction;
    private final Consumer<Pair<World, ItemStack>> itemAction;
    private final int cooldown;

    public ItemUsePower(PowerType<?> type, LivingEntity entity, Predicate<ItemStack> itemCondition, Consumer<Entity> entityAction, Consumer<Pair<World, ItemStack>> itemAction, int cooldown) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.entityAction = entityAction;
        this.itemAction = itemAction;
        this.cooldown = cooldown;
    }

    public void setCooldown(ItemStack stack){
        if(entity instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(stack.getItem(), this.cooldown);
        }
    }

    public boolean doesApply(ItemStack stack) {
        return itemCondition == null || itemCondition.test(stack);
    }

    public void executeActions(ItemStack stack) {
        if (entityAction != null) {
            entityAction.accept(entity);
        }
        if (itemAction != null) {
            itemAction.accept(new Pair<>(entity.world,stack));
        }
        setCooldown(stack);
    }

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("item_use"),
                new SerializableData()
                        .add("cooldown", SerializableDataTypes.INT, 0)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("item_action", ApoliDataTypes.ITEM_ACTION, null),
                data ->
                        (type, entity) -> new ItemUsePower(type, entity, data.get("item_condition"), data.get("entity_action"), data.get("item_action"), data.getInt("cooldown")))
                .allowCondition();
    }

}