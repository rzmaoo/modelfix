package com.rzmao.modelfix.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin {

    @Unique private static final float EXPANSION = 0.008F;
    @Unique private static final float INDENT = 0.007F;

    @ModifyReturnValue(method = "createSideElements", at = @At("RETURN"))
    private static List<BlockElement> createSideElements(List<BlockElement> original) {
        for (BlockElement element : original) {
            Vector3f from = (Vector3f) element.from();
            Vector3f to = (Vector3f) element.to();

            var faces = element.faces();
            if (faces.size() == 1) {
                Direction dir = faces.keySet().iterator().next();

                // Geometric correction algorithm
                switch (dir) {
                    case UP -> {
                        from.add(-EXPANSION, -INDENT, -EXPANSION);
                        to.add(EXPANSION, -INDENT, EXPANSION);
                    }
                    case DOWN -> {
                        from.add(-EXPANSION, INDENT, -EXPANSION);
                        to.add(EXPANSION, INDENT, EXPANSION);
                    }
                    case WEST -> { // Left
                        from.add(-INDENT, EXPANSION, -EXPANSION);
                        to.add(-INDENT, -EXPANSION, EXPANSION);
                    }
                    case EAST -> { // Right
                        from.add(INDENT, EXPANSION, -EXPANSION);
                        to.add(INDENT, -EXPANSION, EXPANSION);
                    }
                }
            }
        }
        return original;
    }

    /**
     * @author RZMAO
     * @reason Optimize span generation logic
     */
    @Overwrite
    private static void createOrExpandSpan(List<ItemModelGenerator.Span> listSpans, ItemModelGenerator.SpanFacing spanFacing, int pixelX, int pixelY) {
        boolean isHorizontal = (spanFacing == ItemModelGenerator.SpanFacing.DOWN || spanFacing == ItemModelGenerator.SpanFacing.UP);

        ItemModelGenerator.Span existingSpan = null;

        for (ItemModelGenerator.Span span : listSpans) {
            if (span.getFacing() == spanFacing) {
                int currentLine = isHorizontal ? pixelY : pixelX;

                if (span.getAnchor() == currentLine) {
                    int currentPos = isHorizontal ? pixelX : pixelY;
                    if (span.getMax() != currentPos - 1) {
                        continue;
                    }
                    existingSpan = span;
                    break;
                }
            }
        }

        int length = isHorizontal ? pixelX : pixelY;
        if (existingSpan == null) {
            int newStart = isHorizontal ? pixelY : pixelX;
            listSpans.add(new ItemModelGenerator.Span(spanFacing, length, newStart));
        } else {
            existingSpan.expand(length);
        }
    }
}