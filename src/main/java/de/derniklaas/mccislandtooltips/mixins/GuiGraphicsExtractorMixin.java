package de.derniklaas.mccislandtooltips.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.derniklaas.mccislandtooltips.client.TooltipStyles;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {

    /**
     * Covers item tooltips outside container screens, most notably items hovered in chat,
     * which go through {@code setTooltipForNextFrame(Font, ItemStack, int, int)}.
     */
    @WrapOperation(
            method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
            )
    )
    private Object mccislandtooltips$applyTooltipStyle(ItemStack stack, DataComponentType<?> type, Operation<Object> original) {
        Object style = original.call(stack, type);
        return style != null ? style : TooltipStyles.styleFor(stack);
    }
}
