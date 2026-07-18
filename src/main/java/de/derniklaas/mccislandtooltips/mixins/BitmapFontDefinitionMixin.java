package de.derniklaas.mccislandtooltips.mixins;

import de.derniklaas.mccislandtooltips.client.TooltipStyles;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BitmapProvider.Definition.class)
public class BitmapFontDefinitionMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mccislandtooltips$captureRarityGlyphs(Identifier file, int height, int ascent, int[][] codepointGrid, CallbackInfo ci) {
        TooltipStyles.registerFontGlyphs(file, codepointGrid);
    }
}
