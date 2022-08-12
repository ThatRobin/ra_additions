package io.github.thatrobin.ra_additions.util;

public interface BundleData {
    boolean isBundle();
    void setBundle(boolean value);
    void setBundleMax(int amount);
    int getBundleMax();
}
