package io.github.thatrobin.ra_additions.util;

import com.google.common.collect.Lists;
import io.github.thatrobin.ra_additions.mixins.KeyBindingRegistryImplAccessor;
import net.minecraft.client.option.KeyBinding;

import java.util.List;

public class KeyBindingRegistryImplExtension {

    public static KeyBinding[] removeAndProcess(KeyBinding[] keysAll, KeyBinding... keyBindings) {
        List<KeyBinding> moddedKeyBindings = KeyBindingRegistryImplAccessor.getModdedKeyBindings();
        List<KeyBinding> newKeysAll = Lists.newArrayList(keysAll);
        newKeysAll.removeAll(moddedKeyBindings);
        for (KeyBinding keyBinding : keyBindings) {
            moddedKeyBindings.remove(keyBinding);
        }
        newKeysAll.addAll(moddedKeyBindings);
        return newKeysAll.toArray(new KeyBinding[0]);
    }

}
