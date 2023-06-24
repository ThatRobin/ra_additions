package io.github.thatrobin.ra_additions.mechanics;

import io.github.thatrobin.ra_additions.component.ClaimedLand;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;

public class ModifyBlockRenderMechanic extends Mechanic {

    private BlockState blockState = Blocks.DIAMOND_BLOCK.getDefaultState();

    public ModifyBlockRenderMechanic(MechanicType<?> type, ClaimedLand land) {
        super(type, land);
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Override
    public NbtElement toTag() {
        return NbtHelper.fromBlockState(this.blockState);
    }

    @Override
    public void fromTag(NbtElement tag) {
        this.blockState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), (NbtCompound) tag);
    }
}
