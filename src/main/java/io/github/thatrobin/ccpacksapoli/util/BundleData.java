package io.github.thatrobin.ccpacksapoli.util;

public interface BundleData {
    boolean isBundle();
    void setBundle(boolean value);
    void setBundleMax(int amount);
    int getBundleMax();
}
