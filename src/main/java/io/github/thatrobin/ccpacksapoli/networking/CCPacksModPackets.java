package io.github.thatrobin.ccpacksapoli.networking;

import io.github.thatrobin.ccpacksapoli.CCPacksApoli;
import net.minecraft.util.Identifier;

public class CCPacksModPackets {
    public static final Identifier HANDSHAKE = CCPacksApoli.identifier("handshake");
    public static final Identifier OPEN_CHOICE_SCREEN = CCPacksApoli.identifier("choice_screen");
    public static final Identifier CHOOSE_CHOICE = CCPacksApoli.identifier("choose_choice");
    public static final Identifier CONFIRM_CHOICE = CCPacksApoli.identifier("confirm_powers");
    public static final Identifier CHOICE_LIST = CCPacksApoli.identifier("choice_list");
    public static final Identifier LAYER_LIST = CCPacksApoli.identifier("layer_list");
}
