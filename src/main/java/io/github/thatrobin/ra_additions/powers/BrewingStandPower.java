package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.*;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class BrewingStandPower extends Power implements SidedInventory, NamedScreenHandlerFactory, Nameable {

    private static final int[] TOP_SLOTS = new int[]{3};
    private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 4};
    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    public int brewTime;
    public boolean[] slotsEmptyLastTick;
    public Item itemBrewing;
    public int fuel;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return BrewingStandPower.this.brewTime;
                }
                case 1 -> {
                    return BrewingStandPower.this.fuel;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> BrewingStandPower.this.brewTime = value;
                case 1 -> BrewingStandPower.this.fuel = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };
    @Nullable
    private Text customName;

    public BrewingStandPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
        this.setTicking();
    }

    public Text getContainerName() {
        return Text.translatable("container.brewing");
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BrewingStandScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public void tick() {
        ItemStack itemStack = this.inventory.get(4);
        if (this.fuel <= 0 && itemStack.isOf(Items.BLAZE_POWDER)) {
            this.fuel = 20;
            itemStack.decrement(1);
            this.markDirty();
        }
        boolean bl = BrewingStandPower.canCraft(this.inventory);
        boolean bl2 = this.brewTime > 0;
        ItemStack itemStack2 = this.inventory.get(3);
        if (bl2) {
            --this.brewTime;
            boolean bl3 = this.brewTime == 0;
            if (bl3 && bl) {
                BrewingStandPower.craft(this.entity.getWorld(), this.entity, this.inventory);
                this.markDirty();
            } else if (!bl || !itemStack2.isOf(this.itemBrewing)) {
                this.brewTime = 0;
                this.markDirty();
            }
        } else if (bl && this.fuel > 0) {
            --this.fuel;
            this.brewTime = 400;
            this.itemBrewing = itemStack2.getItem();
            this.markDirty();
        }
        boolean[] bls = this.getSlotsEmpty();
        if (!Arrays.equals(bls, this.slotsEmptyLastTick)) {
            this.slotsEmptyLastTick = bls;
        }
    }

    public NbtElement toTag() {
        NbtCompound compound = new NbtCompound();
        compound.putShort("BrewTime", (short)this.brewTime);
        Inventories.writeNbt(compound, this.inventory);
        compound.putByte("Fuel", (byte)this.fuel);
        return compound;
    }

    public void fromTag(NbtElement tag) {
        if(tag.getType() == NbtElement.COMPOUND_TYPE) {
            NbtCompound compound = (NbtCompound)tag;
            this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
            Inventories.readNbt(compound, this.inventory);
            this.brewTime = compound.getShort("BrewTime");
            this.fuel = compound.getByte("Fuel");
        }
    }

    public int size() {
        return this.inventory.size();
    }

    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public final boolean[] getSlotsEmpty() {
        boolean[] bls = new boolean[3];
        for (int i = 0; i < 3; ++i) {
            if (this.inventory.get(i).isEmpty()) continue;
            bls[i] = true;
        }
        return bls;
    }

    public static boolean canCraft(DefaultedList<ItemStack> slots) {
        ItemStack itemStack = slots.get(3);
        if (itemStack.isEmpty()) {
            return false;
        }
        if (!BrewingRecipeRegistry.isValidIngredient(itemStack)) {
            return false;
        }
        for (int i = 0; i < 3; ++i) {
            ItemStack itemStack2 = slots.get(i);
            if (itemStack2.isEmpty() || !BrewingRecipeRegistry.hasRecipe(itemStack2, itemStack)) continue;
            return true;
        }
        return false;
    }

    public static void craft(World world, Entity pos, DefaultedList<ItemStack> slots) {
        ItemStack itemStack = slots.get(3);
        for (int i = 0; i < 3; ++i) {
            slots.set(i, BrewingRecipeRegistry.craft(itemStack, slots.get(i)));
        }
        itemStack.decrement(1);
        if (itemStack.getItem().hasRecipeRemainder()) {
            ItemStack itemStack2 = new ItemStack(itemStack.getItem().getRecipeRemainder());
            if (itemStack.isEmpty()) {
                itemStack = itemStack2;
            } else {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStack2);
            }
        }
        slots.set(3, itemStack);
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot >= 0 && slot < this.inventory.size()) {
            return this.inventory.get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.inventory.size()) {
            this.inventory.set(slot, stack);
        }
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return isActive();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 3) {
            return BrewingRecipeRegistry.isValidIngredient(stack);
        }
        if (slot == 4) {
            return stack.isOf(Items.BLAZE_POWDER);
        }
        return (stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.GLASS_BOTTLE)) && this.getStack(slot).isEmpty();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (slot == 3) {
            return stack.isOf(Items.GLASS_BOTTLE);
        }
        return true;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("brewing_stand"),
                new SerializableDataExt(),
                data ->
                        (type, entity) -> new BrewingStandPower(type, entity))
                .allowCondition();
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return this.getContainerName();
    }

    @Override
    public Text getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return this.createScreenHandler(syncId, playerInventory);
    }
}
