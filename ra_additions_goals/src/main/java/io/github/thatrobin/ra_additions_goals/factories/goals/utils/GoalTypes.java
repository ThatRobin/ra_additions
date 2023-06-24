package io.github.thatrobin.ra_additions_goals.factories.goals.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apace100.calio.data.MultiJsonDataLoader;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.registries.RAA_Registries;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class GoalTypes extends MultiJsonDataLoader implements IdentifiableResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public GoalTypes() {
        super(GSON, "goals");
    }

    @Override
    protected void apply(Map<Identifier, List<JsonElement>> prepared, ResourceManager manager, Profiler profiler) {
        GoalRegistry.reset();
        prepared.forEach((id, jel) -> {
            try {
                for (JsonElement je : jel) {
                    readTask(id, je, GoalType::new);
                }
            } catch (Exception e) {
                RA_Additions.LOGGER.error("There was a problem reading Goal file " + id.toString() + " (skipping): " + e.getMessage());
            }
        });
        RA_Additions.LOGGER.info("Finished loading goals from data files. Registry contains " + GoalRegistry.size() + " goals.");
    }

    @SuppressWarnings("rawtypes")
    private void readTask(Identifier id, JsonElement je, BiFunction<Identifier, GoalFactory.Instance, GoalType<?>> taskFactory) {
        JsonObject jo = je.getAsJsonObject();
        Identifier factoryId = Identifier.tryParse(JsonHelper.getString(jo, "type"));
        Optional<GoalFactory<? extends Goal>> taskFactory1 = RAA_Registries.TASK_FACTORY.getOrEmpty(factoryId);
        if (taskFactory1.isPresent()) {
            if (RAA_Registries.TASK_FACTORY.containsId(factoryId)) {
                taskFactory1 = RAA_Registries.TASK_FACTORY.getOrEmpty(factoryId);
                if(taskFactory1.isPresent()) {
                    GoalFactory.Instance factoryInstance = taskFactory1.get().read(jo);
                    GoalType<?> type = taskFactory.apply(id, factoryInstance);
                    if (!GoalRegistry.contains(id)) {
                        GoalRegistry.register(id, type);
                    }
                }
            }
        }
    }

    @Override
    public Identifier getFabricId() {
        return RA_Additions.identifier("goals");
    }

}
