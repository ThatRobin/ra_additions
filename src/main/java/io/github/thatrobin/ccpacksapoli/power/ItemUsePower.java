package io.github.thatrobin.ccpacksapoli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
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

}