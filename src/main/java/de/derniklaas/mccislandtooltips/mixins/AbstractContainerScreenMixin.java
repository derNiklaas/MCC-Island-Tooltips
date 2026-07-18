package de.derniklaas.mccislandtooltips.mixins;

import de.derniklaas.mccislandtooltips.client.TooltipStyles;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Shadow
    protected @Nullable Slot hoveredSlot;

    @ModifyArg(
            method = "extractTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V"
            ),
            index = 5
    )
    private @Nullable Identifier mccislandtooltips$applyTooltipStyle(@Nullable Identifier style) {
        if (style == null && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            return TooltipStyles.styleFor(this.hoveredSlot.getItem());
        }
        return style;
    }
}
