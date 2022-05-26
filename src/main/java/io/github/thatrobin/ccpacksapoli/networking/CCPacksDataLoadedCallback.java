package io.github.thatrobin.ccpacksapoli.networking;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface CCPacksDataLoadedCallback {
    Event<CCPacksDataLoadedCallback> EVENT = EventFactory.createArrayBacked(CCPacksDataLoadedCallback.class,
            (listeners) -> (isClient) -> {
                for (CCPacksDataLoadedCallback event : listeners) {
                    event.onDataLoaded(isClient);
                }
            }
    );

    void onDataLoaded(boolean isClient);
}
