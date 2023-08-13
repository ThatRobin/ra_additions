package io.github.thatrobin.ra_additions_experimental.component;

import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import io.github.thatrobin.ra_additions_experimental.factories.mechanics.Mechanic;
import io.github.thatrobin.ra_additions_experimental.factories.mechanics.MechanicRegistry;
import io.github.thatrobin.ra_additions_experimental.factories.mechanics.MechanicType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClaimComponentImpl implements ClaimComponent, ServerTickingComponent {

    @SuppressWarnings("all")
    private World world;
    @SuppressWarnings("all")
    private HashMap<BlockPos, ClaimedLand> claimedLand = new HashMap<>();

    public ClaimComponentImpl(World world) {
        this.world = world;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        claimedLand.clear();
        NbtList claimData = tag.getList("mechanic_data", NbtElement.COMPOUND_TYPE);
        for(int i = 0; i < claimData.size(); i++) {
            NbtCompound curTag = claimData.getCompound(i);
            BlockPos pos = NbtHelper.toBlockPos(curTag.getCompound("Pos"));
            ClaimedLand land;
            if(getAllPos().contains(pos)) {
                land = getLand(pos);
            } else {
                land = new ClaimedLand();
            }
            Identifier type = Identifier.tryParse(curTag.getString("Type"));
            NbtElement data = curTag.get("Data");
            Mechanic mechanic = MechanicRegistry.get(type).create(land);
            mechanic.fromTag(data);
            addLand(pos, land);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList claimData = new NbtList();
        claimedLand.forEach(((blockPos, claimedLand1) -> {
            for (Map.Entry<MechanicType<?>, Mechanic> mechanicTypeMechanicEntry : claimedLand1.entrySet()) {
                NbtCompound compound = new NbtCompound();
                NbtCompound pos = NbtHelper.fromBlockPos(blockPos);
                compound.put("Pos", pos);
                compound.putString("Type", MechanicRegistry.getId(mechanicTypeMechanicEntry.getKey()).toString());
                compound.put("Data", mechanicTypeMechanicEntry.getValue().toTag());
                claimData.add(compound);
            }
        }));
        tag.put("mechanic_data", claimData);
    }

    public void writeClaimedLandToNbt(NbtCompound tag, BlockPos pos) {
        NbtList claimData = new NbtList();
        for (Map.Entry<MechanicType<?>, Mechanic> mechanicTypeMechanicEntry : claimedLand.get(pos).entrySet()) {
            NbtCompound compound = new NbtCompound();
            NbtCompound nbtpos = NbtHelper.fromBlockPos(pos);
            compound.put("Pos", nbtpos);
            compound.putString("Type", MechanicRegistry.getId(mechanicTypeMechanicEntry.getKey()).toString());
            compound.put("Data", mechanicTypeMechanicEntry.getValue().toTag());
            claimData.add(compound);
        }
        tag.put("mechanic_data", claimData);
    }

    @Override
    public void serverTick() {
        this.claimedLand.forEach(((blockPos, claimedLand1) -> {
            claimedLand1.getPowers().forEach(Mechanic::tick);
        }));
    }

    @Override
    public void syncWithAll() {
        this.world.syncComponent(ClaimComponent.CLAIM_DATA);
    }

    @Override
    public void addLand(BlockPos pos, ClaimedLand land) {
        claimedLand.put(pos, land);
    }

    @Override
    public void removeLand(BlockPos pos) {
        claimedLand.remove(pos);
    }

    @Override
    public ClaimedLand getLand(BlockPos pos) {
        return claimedLand.get(pos);
    }

    @Override
    public List<BlockPos> getAllPos() {
        return claimedLand.keySet().stream().toList();
    }

    @Override
    public <T extends Mechanic> List<T> getMechanics(Class<T> powerClass, BlockPos blockPos) {
        List<T> list = new LinkedList<>();
        if(claimedLand.containsKey(blockPos)) {
            for (Mechanic mechanic : claimedLand.get(blockPos).getPowers()) {
                if (powerClass.isAssignableFrom(mechanic.getClass())) {
                    list.add((T) mechanic);
                }
            }
        }
        return list;
    }
}
