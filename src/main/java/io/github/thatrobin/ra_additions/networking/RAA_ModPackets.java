package io.github.thatrobin.ra_additions.networking;

import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.util.Identifier;

public class RAA_ModPackets {
    public static final Identifier HANDSHAKE = RA_Additions.identifier("handshake");
    public static final Identifier OPEN_CHOICE_SCREEN = RA_Additions.identifier("choice_screen");
    public static final Identifier CHOOSE_CHOICE = RA_Additions.identifier("choose_choice");
    public static final Identifier CONFIRM_CHOICE = RA_Additions.identifier("confirm_powers");
    public static final Identifier CHOICE_LIST = RA_Additions.identifier("choice_list");
    public static final Identifier SEND_KEYBINDS = RA_Additions.identifier("send_keybinds");
    public static final Identifier LAYER_LIST = RA_Additions.identifier("layer_list");
}
