package de.derniklaas.mccislandtooltips.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import de.derniklaas.mccislandtooltips.client.TooltipStyles;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {

    /**
     * Every tooltip overload ends up here, so this is the one place that sees a tooltip nobody
     * gave a style to. The lines are already laid out at this point, so the style glyphs are read
     * back out of them.
     */
    @ModifyVariable(method = "setTooltipForNextFrameInternal", at = @At("HEAD"), argsOnly = true)
    private Identifier mccislandtooltips$applyMissingTooltipStyle(
            Identifier style,
            @Local(argsOnly = true) List<ClientTooltipComponent> lines
    ) {
        if (style != null) {
            return style;
        }
        List<FormattedCharSequence> text = new ArrayList<>(lines.size());
        for (ClientTooltipComponent line : lines) {
            if (line instanceof ClientTextTooltipAccessor accessor) {
                text.add(accessor.mccislandtooltips$getText());
            }
        }
        return TooltipStyles.styleFor(text);
    }
}
