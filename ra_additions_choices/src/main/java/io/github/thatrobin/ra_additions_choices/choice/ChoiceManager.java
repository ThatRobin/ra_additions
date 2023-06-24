package io.github.thatrobin.ra_additions_choices.choice;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.calio.data.MultiJsonDataLoader;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ChoiceManager extends MultiJsonDataLoader implements IdentifiableResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public ChoiceManager() {
        super(GSON, "choices");
    }

    @Override
    protected void apply(Map<Identifier, List<JsonElement>> loader, ResourceManager manager, Profiler profiler) {
        ChoiceRegistry.reset();
        loader.forEach((id, jel) -> jel.forEach(je -> {
            try {
                Choice choice = Choice.fromJson(id, je.getAsJsonObject());
                if(!ChoiceRegistry.contains(id)) {
                    ChoiceRegistry.register(id, choice);
                }
            } catch(Exception e) {
                RA_Additions.LOGGER.error("There was a problem reading Choice file " + id.toString() + " (skipping): " + e.getMessage());
            }
        }));
        RA_Additions.LOGGER.info("Finished loading choices from data files. Registry contains " + ChoiceRegistry.size() + " choices.");
    }

    @Override
    public Identifier getFabricId() {
        return RA_Additions.identifier("choices");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return ImmutableList.of(Apoli.identifier("powers"));
    }
}