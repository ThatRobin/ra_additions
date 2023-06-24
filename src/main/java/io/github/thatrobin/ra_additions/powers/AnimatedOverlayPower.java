package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class AnimatedOverlayPower extends Power {

    private final List<Identifier> textures;
    private final Identifier texture;
    private int textureID;
    private final int interval;
    private Integer initialTicks = null;

    public AnimatedOverlayPower(PowerType<?> type, LivingEntity entity, Identifier texture, List<Identifier> textures, int interval) {
        super(type, entity);
        this.texture = texture;
        this.textures = textures;
        this.interval = interval;
        if(texture == null) {
            this.setTicking();
        }
    }

    @Override
    public void tick() {
        if(texture == null) {
            if (initialTicks == null) {
                initialTicks = entity.age % interval;
            }
            if (entity.age % interval == initialTicks) {
                if (this.textureID == this.textures.size() - 1) {
                    this.textureID = 0;
                } else {
                    this.textureID++;
                }
                PowerHolderComponent.sync(this.entity);
            }
        }
    }

    public boolean shouldRender() {
        if (texture == null && this.textures.size() > this.textureID) {
            return true;
        }
        return texture != null;
    }

    public Identifier getTexture() {
        if (texture == null && this.textures.size() > this.textureID) {
            return this.textures.get(this.textureID);
        } else {
            return texture;
        }
    }

    @Override
    public NbtElement toTag() {
        return NbtInt.of(this.textureID);
    }

    @Override
    public void fromTag(NbtElement tag) {
        this.textureID = ((NbtInt)tag).intValue();
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("animated_overlay"),
                new SerializableDataExt()
                        .add("interval", "The amount of ticks before swapping to the next texture.", SerializableDataTypes.INT, 20)
                        .add("texture_location", "The texture location to use for the overlay.", SerializableDataTypes.IDENTIFIER, null)
                        .add("texture_locations", "The texture locations to use for the overlay.", SerializableDataTypes.IDENTIFIERS, new LinkedList<>()),
                data ->
                        (type, player) -> {
                            return new AnimatedOverlayPower(type, player, data.getId("texture_location"), data.get("texture_locations"), data.getInt("interval"));
                        })
                .allowCondition();
    }

}
