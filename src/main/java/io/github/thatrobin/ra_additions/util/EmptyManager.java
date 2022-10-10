package io.github.thatrobin.ra_additions.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.github.apace100.calio.data.MultiJsonDataLoader;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.List;
import java.util.Map;

public class EmptyManager extends MultiJsonDataLoader implements IdentifiableResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public EmptyManager() {
        super(GSON, "empty");
    }

    @Override
    public Identifier getFabricId() {
        return RA_Additions.identifier("empty");
    }

    @Override
    protected void apply(Map<Identifier, List<JsonElement>> prepared, ResourceManager manager, Profiler profiler) {

    }
}
