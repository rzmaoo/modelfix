package com.rzmao.modelfix.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin {

    @Shadow public abstract ResourceLocation atlasLocation();

    @Unique
    private static final ResourceLocation BLOCK_ATLAS = ResourceLocation.withDefaultNamespace("textures/atlas/blocks.png");

    @ModifyReturnValue(method = "uvShrinkRatio", at = @At("RETURN"))
    private float uvShrinkRatio(float original) {
        if (this.atlasLocation().equals(BLOCK_ATLAS)) {
            return 0.0F;
        }
        return original;
    }
}