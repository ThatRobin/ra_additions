package io.github.thatrobin.ra_additions_experimental.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_experimental.factories.mechanics.Mechanic;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface ClaimComponent extends AutoSyncedComponent {

    ComponentKey<ClaimComponent> CLAIM_DATA = ComponentRegistry.getOrCreate(RA_Additions.identifier("claim_data"), ClaimComponent.class);

    void syncWithAll();

    void addLand(BlockPos pos, ClaimedLand land);

    @SuppressWarnings("unused")
    void removeLand(BlockPos pos);

    ClaimedLand getLand(BlockPos pos);

    List<BlockPos> getAllPos();

    <T extends Mechanic> List<T> getMechanics(Class<T> powerClass, BlockPos blockPos);

    void writeClaimedLandToNbt(NbtCompound compound, BlockPos pos);

    static <T extends Mechanic> List<T> getMechanics(World world, Class<T> powerClass, BlockPos blockPos) {
        return CLAIM_DATA.get(world).getMechanics(powerClass, blockPos);
    }

}
