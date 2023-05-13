package io.github.thatrobin.ra_additions.client;

import io.github.thatrobin.ra_additions.networking.RAA_ModPacketS2C;
import io.github.thatrobin.ra_additions.util.RenderValuePowerOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RA_AdditionsClient implements ClientModInitializer {

    public static boolean isServerRunningRAA = false;
    public static final EntityModelLayer MODEL_CUBE_LAYER = new EntityModelLayer(new Identifier("entitytesting", "cube"), "main");

    @Override
    public void onInitializeClient() {
        RAA_ModPacketS2C.register();
        HudRenderCallback.EVENT.register(new RenderValuePowerOverlay());
    }
}
