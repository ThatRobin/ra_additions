package io.github.thatrobin.ra_additions.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record KeybindingData(String translationKey, String keyKey, String category) {

    public void toBuffer(PacketByteBuf buf, Identifier identifier) {
        buf.writeString(identifier.toString());
        buf.writeString(this.translationKey);
        buf.writeString(this.keyKey);
        buf.writeString(this.category);
    }

    public static KeybindingData fromBuffer(PacketByteBuf buf) {
        Identifier id = Identifier.tryParse(buf.readString());
        String name = buf.readString();
        String key = buf.readString();
        String category = buf.readString();
        if (id != null) {
            return new KeybindingData("key." + id.getNamespace() + "." + id.getPath(), key, category);
        } else {
            return new KeybindingData(name, key, category);
        }
    }
}
