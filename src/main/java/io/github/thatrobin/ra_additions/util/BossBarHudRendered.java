package io.github.thatrobin.ra_additions.util;


public interface BossBarHudRendered {

    BossBarHudRender getRenderSettings();
    @SuppressWarnings("unused")
    float getFill();
    boolean shouldRender();
}
