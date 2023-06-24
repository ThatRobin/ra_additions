package io.github.thatrobin.ra_additions_tags.data_loaders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public class PowerTagManager implements ResourceReloader, IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public final TagGroupLoader<PowerType<?>> tagLoader = new TagGroupLoader<>(this::get, "tags/powers");
    public volatile Map<Identifier, Collection<PowerType<?>>> tags = Map.of();

    public static PowerTagManager POWER_TAG_LOADER = new PowerTagManager();

    @SuppressWarnings("all")
    public Optional<PowerType<?>> get(Identifier id) {
        return Optional.of(PowerTypeRegistry.get(id));
    }

    public Collection<PowerType<?>> getTagOrEmpty(Identifier id) {
        return this.tags.getOrDefault(id, List.of());
    }

    public Stream<Identifier> getTags() {
        return this.tags.keySet().stream();
    }

    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletableFuture<Map<Identifier, List<TagGroupLoader.TrackedEntry>>> completableFuture = CompletableFuture.supplyAsync(() -> this.tagLoader.loadTags(manager), prepareExecutor);
        CompletableFuture<Map<Identifier, CompletableFuture<PowerType<?>>>> completableFuture2 = CompletableFuture.supplyAsync(() -> manager.findResources("powers", (id) -> id.getPath().endsWith(".json")), prepareExecutor).thenCompose((ids) -> {
            Map<Identifier, CompletableFuture<PowerType<?>>> map = Maps.newHashMap();

            CompletableFuture<?>[] completableFutures = (CompletableFuture<?>[])map.values().toArray(new CompletableFuture[0]);
            return CompletableFuture.allOf(completableFutures).handle((unused, ex) -> map);
        });
        CompletableFuture<Pair<Map<Identifier, List<TagGroupLoader.TrackedEntry>>, Map<Identifier, CompletableFuture<PowerType<?>>>>> var10000 = completableFuture.thenCombine(completableFuture2, Pair::of);
        Objects.requireNonNull(synchronizer);
        return var10000.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((intermediate) -> {
            Map<Identifier, CompletableFuture<PowerType<?>>> map = intermediate.getSecond();
            com.google.common.collect.ImmutableMap.Builder<Identifier, PowerType<?>> builder = ImmutableMap.builder();
            map.forEach((id, powerTypeCompletableFuture) -> powerTypeCompletableFuture.handle((function, ex) -> {
                if (ex != null) {
                    LOGGER.error("Failed to load power {}", id, ex);
                } else {
                    builder.put(id, function);
                }

                return null;
            }).join());
            this.tags = this.tagLoader.buildGroup(intermediate.getFirst());
            Apoli.LOGGER.info(this.tags);
        }, applyExecutor);
    }

    @Override
    public Identifier getFabricId() {
        return RA_Additions.identifier("power_tags");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return ImmutableList.of(Apoli.identifier("powers"));
    }
}
