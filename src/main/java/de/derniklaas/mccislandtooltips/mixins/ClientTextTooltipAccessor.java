package de.derniklaas.mccislandtooltips.mixins;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Gives access to the text of an already laid out tooltip line, so style glyphs can still be
 * found once a tooltip has been flattened into {@code ClientTooltipComponent}s.
 */
@Mixin(ClientTextTooltip.class)
public interface ClientTextTooltipAccessor {

    @Accessor("text")
    FormattedCharSequence mccislandtooltips$getText();
}
