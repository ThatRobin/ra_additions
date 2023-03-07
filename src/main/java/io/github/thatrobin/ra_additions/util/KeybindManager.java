package io.github.thatrobin.ra_additions.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.calio.data.MultiJsonDataLoader;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.data.RAA_DataTypes;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KeybindManager extends MultiJsonDataLoader implements IdentifiableResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public KeybindManager() {
        super(GSON, "keybinds");
    }

    @Override
    public Identifier getFabricId() {
        return RA_Additions.identifier("keybinds");
    }

    @Override
    protected void apply(Map<Identifier, List<JsonElement>> prepared, ResourceManager manager, Profiler profiler) {
        //KeybindRegistry.clear();
        prepared.forEach((id, jel) -> jel.forEach(je -> {
            try {
                KeybindingData key = RAA_DataTypes.KEYBINDING.read(je);
                if(!KeybindRegistry.contains(id)) {
                    KeybindRegistry.registerServer(id, key);
                }
            } catch(Exception e) {
                RA_Additions.LOGGER.error("There was a problem reading a KeyBinding file: " + id.toString() + " (skipping): " + e.getMessage());
            }
        }));
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return ImmutableList.of(Apoli.identifier("powers"));
    }
}
