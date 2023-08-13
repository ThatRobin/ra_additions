package io.github.thatrobin.ra_additions.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationProcessor;

import java.io.PrintStream;

@Mixin(value = AnimationProcessor.class, remap = false)
public class AnimationProcessorMixin<T extends GeoAnimatable> {

    @Redirect(method = "buildAnimationQueue", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V"))
    private void stopLogging(PrintStream instance, String x) {

    }
}
