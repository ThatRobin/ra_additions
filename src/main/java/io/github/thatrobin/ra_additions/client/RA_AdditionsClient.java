package io.github.thatrobin.ra_additions.client;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.networking.RAA_ModPacketS2C;
import io.github.thatrobin.ra_additions.powers.BorderPower;
import io.github.thatrobin.ra_additions.util.RenderBorderPower;
import io.github.thatrobin.ra_additions.util.RenderStatBarPowerOverlay;
import io.github.thatrobin.ra_additions.util.RenderValuePowerOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class RA_AdditionsClient implements ClientModInitializer {

    public static boolean isServerRunningRAA = false;

    @Override
    public void onInitializeClient() {
        RAA_ModPacketS2C.register();
        HudRenderCallback.EVENT.register(new RenderValuePowerOverlay());
        HudRenderCallback.EVENT.register(new RenderStatBarPowerOverlay());

        WorldRenderEvents.LAST.register(RA_Additions.identifier("render_border"), (context) -> {
            if (MinecraftClient.getInstance().world != null) {
                Iterable<Entity> entities = MinecraftClient.getInstance().world.getEntities();
                for(Entity entity : entities) {
                    if(entity instanceof LivingEntity livingEntity) {
                        PowerHolderComponent component = PowerHolderComponent.KEY.get(livingEntity);
                        for (BorderPower power : component.getPowers(BorderPower.class)) {
                            RenderBorderPower.renderWorldBorder(context.camera(), power);
                        }
                    }
                }
            }
        });
    }

}
