package io.github.thatrobin.ccpacksapoli.mixins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ccpacksapoli.power.BundlePower;
import io.github.thatrobin.ccpacksapoli.util.BundleData;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Mixin(Item.class)
public class ItemMixin implements BundleData {

    public int hold_amount;
    public boolean isBundle;

    @Inject(method = "onStackClicked", at = @At("HEAD"), cancellable = true)
    public void onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if(isBundle()) {
            if (clickType == ClickType.RIGHT) {
                ItemStack itemStack = slot.getStack();
                if (itemStack.isEmpty()) {
                    this.playRemoveOneSound(player);
                    removeFirstStack(stack).ifPresent((removedStack) -> {
                        addToBundle(stack, slot.insertStack(removedStack));
                    });
                } else if (itemStack.getItem().canBeNested()) {
                    int i = (getBundleMax() - getBundleOccupancy(stack)) / getItemOccupancy(itemStack);
                    int j = addToBundle(stack, slot.takeStackRange(itemStack.getCount(), i, player));
                    if (j > 0) {
                        this.playInsertSound(player);
                    }
                }

                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    public void onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if(isBundle()) {
            if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {
                if (otherStack.isEmpty()) {
                    removeFirstStack(stack).ifPresent((itemStack) -> {
                        this.playRemoveOneSound(player);
                        cursorStackReference.set(itemStack);
                    });
                } else {
                    int i = addToBundle(stack, otherStack);
                    if (i > 0) {
                        this.playInsertSound(player);
                        otherStack.decrement(i);
                    }
                }

                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true)
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if(entity instanceof PlayerEntity playerEntity) {
            if(PowerHolderComponent.hasPower(playerEntity, BundlePower.class)) {
                List<BundlePower> powers = PowerHolderComponent.getPowers(playerEntity, BundlePower.class);
                for(BundlePower power : powers) {
                    if (power.doesApply(stack)) {
                        setBundle(true);
                        setBundleMax(power.hold_amount);
                    }
                }
            } else {
                setBundle(false);
                setBundleMax(64);
            }
        }
    }

    private int addToBundle(ItemStack bundle, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem().canBeNested()) {
            NbtCompound nbtCompound = bundle.getOrCreateNbt();
            if (!nbtCompound.contains("Items")) {
                nbtCompound.put("Items", new NbtList());
            }

            int i = getBundleOccupancy(bundle);
            int j = getItemOccupancy(stack);
            int k = Math.min(stack.getCount(), (getBundleMax() - i) / j);
            if (k == 0) {
                return 0;
            } else {
                NbtList nbtList = nbtCompound.getList("Items", 10);
                Optional<NbtCompound> optional = canMergeStack(stack, nbtList);
                if (optional.isPresent()) {
                    NbtCompound nbtCompound2 = (NbtCompound)optional.get();
                    ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                    itemStack.increment(k);
                    itemStack.writeNbt(nbtCompound2);
                    nbtList.remove(nbtCompound2);
                    nbtList.add(0, nbtCompound2);
                } else {
                    ItemStack nbtCompound2 = stack.copy();
                    nbtCompound2.setCount(k);
                    NbtCompound itemStack = new NbtCompound();
                    nbtCompound2.writeNbt(itemStack);
                    nbtList.add(0, itemStack);
                }

                return k;
            }
        } else {
            return 0;
        }
    }

    private static Optional<NbtCompound> canMergeStack(ItemStack stack, NbtList items) {
        if (stack.isOf(Items.BUNDLE)) {
            return Optional.empty();
        } else {
            Stream var10000 = items.stream();
            Objects.requireNonNull(NbtCompound.class);
            var10000 = var10000.filter(NbtCompound.class::isInstance);
            Objects.requireNonNull(NbtCompound.class);
            return var10000.map(NbtCompound.class::cast).filter((item) -> {
                return ItemStack.canCombine(ItemStack.fromNbt((NbtCompound) item), stack) && (ItemStack.fromNbt((NbtCompound) item).getCount() + stack.getCount() <= stack.getMaxCount());
            }).findFirst();
        }
    }

    private int getItemOccupancy(ItemStack stack) {
        return 1;
    }

    private int getBundleOccupancy(ItemStack stack) {
        return getBundledStacks(stack).mapToInt((itemStack) -> {
            return getItemOccupancy(itemStack) * itemStack.getCount();
        }).sum();
    }

    private static Optional<ItemStack> removeFirstStack(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return Optional.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", 10);
            if (nbtList.isEmpty()) {
                return Optional.empty();
            } else {
                NbtCompound nbtCompound2 = nbtList.getCompound(0);
                ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                nbtList.remove(0);
                if (nbtList.isEmpty()) {
                    stack.removeSubNbt("Items");
                }

                return Optional.of(itemStack);
            }
        }
    }

    private static Stream<ItemStack> getBundledStacks(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return Stream.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", 10);
            Stream var10000 = nbtList.stream();
            Objects.requireNonNull(NbtCompound.class);
            return var10000.map(NbtCompound.class::cast).map((nbt) -> ItemStack.fromNbt((NbtCompound) nbt));
        }
    }

    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    public void getTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir) {
        if (isBundle()) {
            DefaultedList<ItemStack> defaultedList = DefaultedList.of();
            Stream var10000 = getBundledStacks(stack);
            Objects.requireNonNull(defaultedList);
            var10000.forEach((stack2) -> defaultedList.add((ItemStack) stack2));
            cir.setReturnValue(Optional.of(new BundleTooltipData(defaultedList, getBundleMax())));
        } else if ((Item) (Object) this instanceof BundleItem) {
            DefaultedList<ItemStack> defaultedList = DefaultedList.of();
            Stream var10000 = getBundledStacks(stack);
            Objects.requireNonNull(defaultedList);
            var10000.forEach((stack2) -> defaultedList.add((ItemStack) stack2));
            cir.setReturnValue(Optional.of(new BundleTooltipData(defaultedList, 64)));
        }
    }

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if(isBundle()) {
            tooltip.add((new TranslatableText("item.minecraft.bundle.fullness", new Object[]{getBundleOccupancy(stack), getBundleMax()})).formatted(Formatting.GRAY));
        }
    }

    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemUsage.spawnItemContents(entity, getBundledStacks(entity.getStack()));
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    @Override
    public boolean isBundle() {
        return this.isBundle;
    }

    @Override
    public void setBundle(boolean value) {
        this.isBundle = value;
    }

    @Override
    public void setBundleMax(int amount) {
        this.hold_amount = amount;
    }

    @Override
    public int getBundleMax() {
        return this.hold_amount;
    }
}
