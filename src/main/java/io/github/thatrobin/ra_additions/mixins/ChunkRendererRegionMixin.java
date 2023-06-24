package io.github.thatrobin.ra_additions.mixins;

import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.component.ClaimComponent;
import io.github.thatrobin.ra_additions.mechanics.ModifyBlockRenderMechanic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ChunkRendererRegion.class)
public class ChunkRendererRegionMixin {

    @Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true)
    private void modifyBlockRender(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        World world = MinecraftClient.getInstance().world;
        if(world != null) {
            for (ModifyBlockRenderMechanic mechanic : ClaimComponent.getMechanics(world, ModifyBlockRenderMechanic.class, pos)) {
                RA_Additions.LOGGER.info(mechanic.getBlockState().toString());
                cir.setReturnValue(mechanic.getBlockState());
                return;
            }
        }
    }

}
